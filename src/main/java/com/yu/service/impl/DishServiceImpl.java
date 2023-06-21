package com.yu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.QiniuException;
import com.yu.dao.DishDao;
import com.yu.domain.Category;
import com.yu.domain.Dish;
import com.yu.domain.DishFlavor;
import com.yu.dto.DishDto;
import com.yu.service.CategoryService;
import com.yu.service.DishFlavorService;
import com.yu.service.DishService;
import com.yu.service.qiniu.QiniuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishDao dishDao;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Value("${Image.path}")
//    private String path;

    /**
     * 新增菜品同时加上口味数据
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存dish
        this.save(dishDto);

        //保存dishFlavor
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id得到菜品信息和口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithDishDto(Long id) {
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);

        List<DishFlavor> dishFlavors = dishFlavorService.list(lqw);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        Long categoryId = dishDto.getCategoryId();
        Category category = categoryService.getById(categoryId);
        dishDto.setCategoryName(category.getName());
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    /**
     * 更新菜品的数据、口味和种类
     * @param dishDto
     * @return
     */
    @Override
    public boolean updateWithDishDto(DishDto dishDto) {
        //拷贝dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        //拷贝category
        Category category = new Category();
        category.setName(dishDto.getCategoryName());
        category.setId(dishDto.getCategoryId());

        //删除菜品的口味
        Long id = dish.getId();
        LambdaQueryWrapper<DishFlavor> lqw=new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(lqw);
        //拷贝flavors
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            if (flavor.getDishId()==null)
                flavor.setDishId(id);
            dishFlavorService.save(flavor);
        }
        this.updateById(dish);
        categoryService.updateById(category);
        return true;
    }

    /**
     * 分页条件查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page getByPage(int page, int pageSize, String name) {
        Page<Dish> p = new Page(page, pageSize);
        Page<DishDto> pd = new Page<>();
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(name), Dish::getName, name == null ? null : name.trim());

        this.page(p, lqw);
        BeanUtils.copyProperties(p, pd, "records");

        List<Dish> records = p.getRecords();
        List<DishDto> list = records.stream().map((record) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record, dishDto);
            Long categoryId = record.getCategoryId();
            if (categoryId != null) {
                Category category = categoryService.getById(categoryId);
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
        }).collect(Collectors.toList());
        pd.setRecords(list);
        return pd;
    }

    /**
     * 删除菜品和他的图片 口味
     * @param ids
     * @return
     */
    @Override
    public boolean deleteWithImagesAndFlavor(Long[] ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.select(Dish::getImage, Dish::getId);
            lqw.eq(Dish::getId, id);
            Dish dish = dishDao.selectById(id);
            /*File file=new File(path+dish.getImage());
            file.delete();*/
            //删除图片信息
            try {
                redisTemplate.opsForSet().remove("dishImageResourceCache",dish.getImage());
                redisTemplate.opsForSet().remove("dishImageCache",dish.getImage());
                qiniuService.delete(dish.getImage());
            } catch (QiniuException e) {
                throw new RuntimeException(e);
            }
            //删除数据库的dishDto
            this.removeById(id);
            LambdaQueryWrapper<DishFlavor> df=new LambdaQueryWrapper<>();
            df.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(df);
        }
        return true;
    }

    @Override
    public List<DishDto> listWithDto(Dish dish) {
        LambdaQueryWrapper<Dish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为起售的
        lqw.eq(Dish::getStatus,1);
        //排序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = this.list(lqw);
        List<DishDto> dishDtoList=dishes.stream().map(dish1 -> {
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);

            /*Long categoryId = dish1.getCategoryId();
            if (categoryId!=null) {
                Category category = categoryService.getById(categoryId);
                dishDto.setCategoryName(category.getName());
            }*/
            Long id = dish1.getId();
            LambdaQueryWrapper<DishFlavor> lqwFlavor=new LambdaQueryWrapper<>();
            lqwFlavor.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list = dishFlavorService.list(lqwFlavor);
            dishDto.setFlavors(list);

            return dishDto;
        }).collect(Collectors.toList());
        return dishDtoList;
    }

}

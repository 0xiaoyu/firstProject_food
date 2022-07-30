package com.yu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.dao.DishDao;
import com.yu.domain.Category;
import com.yu.domain.Dish;
import com.yu.domain.DishFlavor;
import com.yu.dto.DishDto;
import com.yu.service.CategoryService;
import com.yu.service.DishFlavorService;
import com.yu.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Value("${Image.path}")
    private String path;

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

    @Override
    public boolean updateWithDishDto(DishDto dishDto) {
        //拷贝dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto, dish);

        //拷贝category
        Category category = new Category();
        category.setName(dishDto.getCategoryName());
        category.setId(dishDto.getCategoryId());

        //拷贝flavors
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> dishFlavors = new ArrayList<>();
        flavors.stream().forEach(dishFlavor -> dishFlavors.add(dishFlavor));

        dishFlavorService.updateBatchById(dishFlavors);
        this.updateById(dish);
        categoryService.updateById(category);
        return true;
    }

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

    @Override
    public boolean deleteWithImagesAndFlavor(Long[] ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
            lqw.select(Dish::getImage, Dish::getId);
            lqw.eq(Dish::getId, id);
            Dish dish = dishDao.selectById(id);
            File file=new File(path+dish.getImage());
            file.delete();
            this.removeById(id);
            LambdaQueryWrapper<DishFlavor> df=new LambdaQueryWrapper<>();
            df.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(df);
        }
        return true;
    }

}

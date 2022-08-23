package com.yu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
import com.yu.domain.Dish;
import com.yu.dto.DishDto;
import com.yu.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 分页获取菜品
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> backendgetByPage(int page, int pageSize, String name) {
        Page pd = dishService.getByPage(page, pageSize, name);
        return R.success(pd);
    }

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> backendsave(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        redisTemplate.opsForSet().add("dishImageCache",dishDto.getImage());
        return R.success("新增成功");
    }

    /**
     * 根据id查询菜品消息和口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithDishDto(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithDishDto(id);
        return R.success(dishDto);
    }

    /**
     * 更新菜品和口味
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    @CacheEvict(value = "dishCache",key = "#dishDto.categoryId+'_1'")
    public R<String> backendupdateWithDishDto(@RequestBody DishDto dishDto) {
        boolean b = dishService.updateWithDishDto(dishDto);
        if (b)
            return R.success("更新成功");
        else
            return R.error("失败了");
    }

    /**
     * 修改状态
     *
     * @param ids
     * @param status
     * @return
     */
    @PostMapping("/status/{status}")
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> backendstopDish(Long[] ids, @PathVariable int status) {
        List<Dish> dishes = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishes.add(dish);
        }
        dishService.updateBatchById(dishes);
        return R.success("修改成功");
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> backenddeleteByIds(Long[] ids) {
        dishService.deleteWithImagesAndFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件获取菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "#dish.categoryId+'_'+#dish.status")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.listWithDto(dish);
        if (dishDtoList != null)
            return R.success(dishDtoList);
        else
            return R.error("出错了");
    }
}

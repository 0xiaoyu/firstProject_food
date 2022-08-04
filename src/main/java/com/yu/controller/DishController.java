package com.yu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
import com.yu.domain.Dish;
import com.yu.dto.DishDto;
import com.yu.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redis;

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
    public R<String> backendupdateWithDishDto(@RequestBody DishDto dishDto) {
        boolean b = dishService.updateWithDishDto(dishDto);
        if (b) {
            Set keys = redis.keys("dish_" + dishDto.getCategoryId() + "_1");
            redis.delete(keys);
            return R.success("更新成功");
        }
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
    public R<String> backendstopDish(Long[] ids, @PathVariable int status) {
        List<Dish> dishes = new ArrayList<>();
        for (Long id : ids) {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishes.add(dish);
        }
        redis.delete("dish_*");
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
    public R<String> backenddeleteByIds(Long[] ids) {
        dishService.deleteWithImagesAndFlavor(ids);
        redis.delete("dish_*");
        return R.success("删除成功");
    }

    /**
     * 根据条件获取菜品
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        dishDtoList= (List<DishDto>) redis.opsForValue().get(key);
        System.out.println(dishDtoList);
        if (dishDtoList!=null)
            return R.success(dishDtoList);
        dishDtoList = dishService.listWithDto(dish);
        if (dishDtoList != null) {
            redis.opsForValue().set(key, dishDtoList,60, TimeUnit.HOURS);
            return R.success(dishDtoList);
        }
        else
            return R.error("出错了");
    }
}

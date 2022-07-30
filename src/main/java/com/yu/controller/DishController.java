package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/page")
    public R<Page> getByPage(int page, int pageSize, String name) {
        Page pd=dishService.getByPage(page,pageSize,name);
        return R.success(pd);
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增成功");
    }

    @GetMapping("/{id}")
    public R<DishDto> getByIdWithDishDto(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithDishDto(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateWithDishDto(@RequestBody DishDto dishDto) {
        boolean b = dishService.updateWithDishDto(dishDto);
        if (b)
            return R.success("更新成功");
        else
            return R.error("失败了");
    }

    @PostMapping("/status/{status}")
    public R<String> stopDish(Long[] ids,@PathVariable int status){
        System.out.println(Arrays.toString(ids));
        List<Dish> dishes=new ArrayList<>();
        for (Long id : ids) {
            Dish dish=new Dish();
            dish.setId(id);
            dish.setStatus(status);
            dishes.add(dish);
        }
        dishService.updateBatchById(dishes);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteByIds(Long[] ids){
        dishService.deleteWithImagesAndFlavor(ids);
        return R.success("删除成功");
    }


}

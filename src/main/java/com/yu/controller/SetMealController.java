package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.R;
import com.yu.domain.SetMeal;
import com.yu.domain.SetmealDish;
import com.yu.dto.SetmealDto;
import com.yu.dto.MealDto;
import com.yu.service.DishService;
import com.yu.service.SetMealService;
import com.yu.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("setmeal")
public class SetMealController {

    @Autowired
    private SetMealService service;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> backendgetByPage(int page, int pageSize, String name) {
        Page p = service.getByPage(page, pageSize, name);
        return R.success(p);
    }

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> backendsave(@RequestBody SetmealDto setmealDto) {
        service.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> backenddelete(Long[] ids) {
        service.deleteWith(ids);
        return R.success("删除成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> backendupdateStatus(@PathVariable int status,Long[] ids ) {
        for (Long id : ids) {
            SetMeal sm = new SetMeal();
            sm.setId(id);
            sm.setStatus(status);
            service.updateById(sm);
        }
        return R.success("修改成功");
    }

    /**
     * 用户获取套餐信息
     * @param setMeal
     * @return
     */
    @GetMapping("/list")
    public R<List<SetMeal>> list(SetMeal setMeal){
        LambdaQueryWrapper<SetMeal> lqw=new LambdaQueryWrapper<>();
        lqw.eq(setMeal.getCategoryId()!=null,SetMeal::getCategoryId,setMeal.getCategoryId());
        lqw.eq(setMeal.getStatus()!=null,SetMeal::getStatus,setMeal.getStatus());
        //排序
        lqw.orderByDesc(SetMeal::getUpdateTime);
        List<SetMeal> setMeals = service.list(lqw);
        return R.success(setMeals);
    }

    /**
     * 获取套餐的食品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<MealDto>> getDishById(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> lqw=new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lqw);
        List<MealDto> meals=setmealDishes.stream().map(d->{
            MealDto meal=new MealDto();
            String dishId = d.getDishId();
            String image = dishService.getById(dishId).getImage();
            meal.setImage(image);
            BeanUtils.copyProperties(d,meal);
            System.out.println(meal);
            return meal;
        }).collect(Collectors.toList());
        return R.success(meals);
    }

    /**
     * 获取套餐的信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = service.selectById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> backendupdate(@RequestBody SetmealDto setmealDto){
        boolean b = service.updateWithDto(setmealDto);
        return b?R.success("修改成功"):R.error("修改失败");
    }

}

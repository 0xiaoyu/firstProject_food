package com.yu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.common.exception.CustomException;
import com.yu.dao.CategoryDao;
import com.yu.dao.DishDao;
import com.yu.domain.Category;
import com.yu.domain.Dish;
import com.yu.domain.SetMeal;
import com.yu.service.CategoryService;
import com.yu.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishDao dishDao;

    @Autowired
    private SetMealService setMealService;

    /**
     * 根据id删除分类
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Dish::getCategoryId, id);
        //查询当前按分类是否关联的菜品
        Long count = dishDao.selectCount(lqw);
        if (count > 0) {
            //已经关联的菜品,抛出异常
            throw new CustomException("当前分类项关联了菜品，无法删除");
        }
        //查询当前按分类是否关联的套餐
        LambdaQueryWrapper<SetMeal> slqw = new LambdaQueryWrapper<>();
        slqw.eq(SetMeal::getCategoryId,id);
        long count1 = setMealService.count(slqw);
        if (count1 > 0) {
            throw new CustomException("当前分类项关联了套餐，无法删除");
        }
        super.removeById(id);
    }
}

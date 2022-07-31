package com.yu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.common.exception.CustomException;
import com.yu.dao.SetMealDao;
import com.yu.domain.Category;
import com.yu.domain.SetMeal;
import com.yu.domain.SetmealDish;
import com.yu.dto.SetmealDto;
import com.yu.service.CategoryService;
import com.yu.service.SetMealService;
import com.yu.service.SetmealDishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealDao, SetMeal> implements SetMealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Value("${Image.path}")
    private String path;

    @Override
    public Page getByPage(int page, int pageSize, String name) {
        Page<SetMeal> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<SetMeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(!StringUtils.isEmpty(name), SetMeal::getName, name == null ? null : name.trim());
        lqw.orderByDesc(SetMeal::getUpdateTime);
        this.page(p, lqw);

        Page<SetmealDto> sp = new Page<>();
        BeanUtils.copyProperties(p, sp, "records");
        List<SetMeal> records = p.getRecords();
        List<SetmealDto> list = records.stream().map(record -> {
            SetmealDto sd = new SetmealDto();
            BeanUtils.copyProperties(record, sd);
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                sd.setCategoryName(categoryName);
            }
            return sd;
        }).collect(Collectors.toList());

        sp.setRecords(list);
        return sp;
    }

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(String.valueOf(setmealDto.getId()));
        }
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    public void deleteWith(Long[] ids) {
        for (Long id : ids) {
            SetMeal setMeal = this.getById(id);
            if (setMeal.getStatus()==0)
                throw new CustomException("存在售卖的商品");
            String image = setMeal.getImage();
            this.removeById(id);
            LambdaQueryWrapper<SetmealDish> lqw=new LambdaQueryWrapper<>();
            lqw.eq(SetmealDish::getSetmealId,id);
            setmealDishService.remove(lqw);
            File file=new File(path+image);
            file.delete();
        }
    }


}

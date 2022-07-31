package com.yu.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.dao.SetMealDao;
import com.yu.dao.SetmealDishDao;
import com.yu.domain.SetmealDish;
import com.yu.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishDao, SetmealDish> implements SetmealDishService {
}
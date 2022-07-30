package com.yu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.dao.SetMealDao;
import com.yu.domain.SetMeal;
import com.yu.service.SetMealService;
import org.springframework.stereotype.Service;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealDao, SetMeal> implements SetMealService {
}

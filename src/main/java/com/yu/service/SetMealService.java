package com.yu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.dao.SetMealDao;
import com.yu.domain.SetMeal;
import com.yu.dto.SetmealDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetMealService extends IService<SetMeal>{

    public Page getByPage(int page, int pageSize,String name);

    /**
     * 新增套餐和菜品
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    public void deleteWith(Long[] ids);
}

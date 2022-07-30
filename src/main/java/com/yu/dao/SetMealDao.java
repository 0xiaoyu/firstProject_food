package com.yu.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.domain.SetMeal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealDao extends BaseMapper<SetMeal> {
}

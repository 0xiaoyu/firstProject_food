package com.yu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}

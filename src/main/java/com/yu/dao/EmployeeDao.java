package com.yu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeDao extends BaseMapper<Employee> {

}

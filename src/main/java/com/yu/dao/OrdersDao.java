package com.yu.dao;

import com.yu.domain.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author za'y
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2022-07-31 15:16:22
* @Entity com.yu.domain.Orders
*/
@Mapper
public interface OrdersDao extends BaseMapper<Orders> {

}





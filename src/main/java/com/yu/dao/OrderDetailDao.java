package com.yu.dao;

import com.yu.domain.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author za'y
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2022-07-31 15:16:09
* @Entity com.yu.domain.OrderDetail
*/
@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {

}





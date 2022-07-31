package com.yu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.domain.OrderDetail;
import com.yu.service.OrderDetailService;
import com.yu.dao.OrderDetailDao;
import org.springframework.stereotype.Service;

/**
* @author za'y
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-07-31 15:16:09
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail>
    implements OrderDetailService{

}





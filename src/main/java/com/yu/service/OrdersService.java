package com.yu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.domain.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
* @author za'y
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2022-07-31 15:16:22
*/

@Transactional
public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);

    Page getByPage(int page, int pageSize);
}

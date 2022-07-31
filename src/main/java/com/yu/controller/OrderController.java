package com.yu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.BaseContext;
import com.yu.common.R;
import com.yu.dao.OrdersDto;
import com.yu.domain.Orders;
import com.yu.service.OrderDetailService;
import com.yu.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("userPage")
    public R<Page<OrdersDto>> getByPage(int page, int pageSize){
        Page byPage = ordersService.getByPage(page, pageSize);
        return R.success(byPage);
    }

    @GetMapping("page")
    public R<Page<OrdersDto>> backendPage(int page, int pageSize, Long number, String beginTime,String endTime){

        return null;
    }
}

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

    /**
     * 新增订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 获取用户订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("userPage")
    public R<Page<OrdersDto>> getByPage(int page, int pageSize){
        Page byPage = ordersService.getByPage(page, pageSize);
        return R.success(byPage);
    }

    /**
     * 条件分页查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("page")
    public R<Page<OrdersDto>> backendPage(int page, int pageSize, Long number, String beginTime,String endTime){
        Page<OrdersDto> r = ordersService.backendPage(page, pageSize, number, beginTime, endTime);
        return R.success(r);
    }

    /**
     * 修改订单
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> backendupdate(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改成功");
    }

}

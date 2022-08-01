package com.yu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.common.BaseContext;
import com.yu.common.R;
import com.yu.common.exception.CustomException;
import com.yu.dao.OrdersDto;
import com.yu.domain.*;
import com.yu.service.*;
import com.yu.dao.OrdersDao;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author za'y
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2022-07-31 15:16:22
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders>
        implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private EuserService euserService;

    @Autowired
    private AddressBookService addressBookService;


    @Override
    public void submit(Orders orders) {
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(lqw);

        if (shoppingCarts == null || shoppingCarts.size() == 0)
            throw new CustomException("购物车为空,不能下单");

        Euser user = euserService.getById(userId);

        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null)
            throw new CustomException("地址不能为空");
        Long id = IdWorker.getId();

        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(item, orderDetail);
            orderDetail.setOrderId(id);
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setNumber(String.valueOf(id));
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getEmail());
        orders.setUserName(user.getName());
        orders.setId(id);
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);


        orderDetailService.saveBatch(orderDetails);

        shoppingCartService.remove(lqw);

    }

    @Override
    public Page getByPage(int page, int pageSize) {
        Page<Orders> p = new Page<>(page, pageSize);
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Orders::getUserId, userId);
        this.page(p, lqw);

        Page<OrdersDto> op = new Page<>();
        BeanUtils.copyProperties(p, op, "records");

        List<Orders> records = p.getRecords();
        List<OrdersDto> ordersDtos = records.stream().map(item -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(lambdaQueryWrapper);
            dto.setOrderDetails(orderDetails);

            LambdaQueryWrapper<AddressBook> lbook = new LambdaQueryWrapper<>();
            lbook.eq(AddressBook::getId, item.getAddressBookId());
            AddressBook one = addressBookService.getOne(lbook);
            BeanUtils.copyProperties(one, dto);
            return dto;
        }).collect(Collectors.toList());
        op.setRecords(ordersDtos);
        return op;
    }

    @Override
    public Page<OrdersDto> backendPage(int page, int pageSize, Long number, String beginTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Page<Orders> p1=new Page<>(page,pageSize);
        //分页查询orders表,注意时间范围
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();
        lqw.ge(beginTime!=null,Orders::getOrderTime, beginTime)
                .le(endTime!=null,Orders::getOrderTime, endTime);
        lqw.like(number!=null,Orders::getNumber,number);
        this.page(p1,lqw);
        //拷贝order信息
        Page<OrdersDto> p=new Page<>();
        BeanUtils.copyProperties(p1,p,"records");
        List<Orders> orders = p1.getRecords();
        //遍历得到每一个订单，在addressBook中查询其用户信息添加其中
        List<OrdersDto> ordersDtos=orders.stream().map(o->{
            OrdersDto ordersDto=new OrdersDto();
            BeanUtils.copyProperties(o,ordersDto);
            Long addressBookId = o.getAddressBookId();
            AddressBook addressBook = addressBookService.getById(addressBookId);
            BeanUtils.copyProperties(addressBook,ordersDto);
            ordersDto.setUserName(addressBook.getConsignee());
            return ordersDto;
        }).collect(Collectors.toList());
        p.setRecords(ordersDtos);
        return p;
    }
}





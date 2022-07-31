package com.yu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yu.common.BaseContext;
import com.yu.common.R;
import com.yu.domain.ShoppingCart;
import com.yu.service.ShoppingCartService;
import com.yu.service.impl.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class shoppingCartController {

    @Autowired
    private ShoppingCartService service;

    @GetMapping("/list")
    public R<List<ShoppingCart>> getList() {
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, id);
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = service.list(lqw);
        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            lqw.eq(ShoppingCart::getDishId, dishId);
        } else {
            lqw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = service.getOne(lqw);
        if (one != null) {
            one.setNumber(one.getNumber() + 1);
            service.updateById(one);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            service.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart one=null;
        LambdaQueryWrapper<ShoppingCart> lqw=new LambdaQueryWrapper<>();
        if (dishId != null) {
            lqw.eq(ShoppingCart::getUserId,currentId);
            lqw.eq(ShoppingCart::getDishId,dishId);
            one = service.getOne(lqw);

        }else {
            Long setmealId = shoppingCart.getSetmealId();
            lqw.eq(ShoppingCart::getUserId,currentId);
            lqw.eq(ShoppingCart::getSetmealId,setmealId);
            one = service.getOne(lqw);
        }
        Integer number = one.getNumber();
        one.setNumber(number-1);
        if (number==0){
            service.remove(lqw);
        }else {
            service.updateById(one);
        }
        return R.success(one);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        Long id = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, id);
        service.remove(lqw);
        return R.success("清空成功");
    }


}

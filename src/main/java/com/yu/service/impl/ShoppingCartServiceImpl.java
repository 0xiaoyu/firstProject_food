package com.yu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.domain.ShoppingCart;
import com.yu.service.ShoppingCartService;
import com.yu.dao.ShoppingCartDao;
import org.springframework.stereotype.Service;

/**
* @author za'y
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2022-07-31 14:30:19
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart>
    implements ShoppingCartService{

}





package com.yu.dao;

import com.yu.domain.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author za'y
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2022-07-31 14:30:19
* @Entity com.yu.domain.ShoppingCart
*/
@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {

}





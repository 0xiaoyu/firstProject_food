package com.yu.dao;

import com.yu.domain.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author za'y
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2022-07-31 10:24:54
* @Entity com.yu.domain.AddressBook
*/
@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {

}





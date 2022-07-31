package com.yu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.domain.AddressBook;
import com.yu.service.AddressBookService;
import com.yu.dao.AddressBookDao;
import org.springframework.stereotype.Service;

/**
* @author za'y
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-07-31 10:24:54
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook>
    implements AddressBookService{

}





package com.yu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yu.dao.DishFlavorDao;
import com.yu.domain.DishFlavor;
import com.yu.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}

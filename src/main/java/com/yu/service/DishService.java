package com.yu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.domain.Dish;
import com.yu.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface DishService extends IService<Dish> {

    //保存菜品相关数据
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品相关数据
    DishDto getByIdWithDishDto(Long id);

    //更新菜品相关数据
    boolean updateWithDishDto(DishDto dishDto);

    //分页查询
    Page getByPage(int page, int pageSize, String name);


    //删除菜品数据，并删除图片信息
    boolean deleteWithImagesAndFlavor(Long[] ids);

    //获取所有的菜品数据
    List<DishDto> listWithDto(Dish dish);
}

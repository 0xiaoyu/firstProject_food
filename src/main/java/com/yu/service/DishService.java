package com.yu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yu.domain.Dish;
import com.yu.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithDishDto(Long id);

    boolean updateWithDishDto(DishDto dishDto);

    Page getByPage(int page, int pageSize, String name);


    boolean deleteWithImagesAndFlavor(Long[] ids);


    List<DishDto> listWithDto(Dish dish);
}

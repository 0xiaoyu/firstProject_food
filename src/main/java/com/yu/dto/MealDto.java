package com.yu.dto;

import com.yu.domain.SetmealDish;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MealDto extends SetmealDish {

    private String name;

    private BigDecimal price;

    private Integer copies;

    private String image;
}

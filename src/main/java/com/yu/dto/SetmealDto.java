package com.yu.dto;

import com.yu.domain.SetMeal;
import com.yu.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends SetMeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}

package com.yu;

import com.yu.service.DishService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private DishService dishService;

    @Test
    void contextLoads() {
    }

}

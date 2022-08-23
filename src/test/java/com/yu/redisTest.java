package com.yu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@SpringBootTest
public class redisTest {

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redis;

    @Test
    public void test() {
        Set<String> set = redis.opsForSet().difference("dishImageResourceCache","dishImageCache");
        Set cache = redis.opsForSet().members("dishImageResourceCache");
        System.out.println(cache);
        for (String s : set) {
            System.out.println(s);
        }

    }
}

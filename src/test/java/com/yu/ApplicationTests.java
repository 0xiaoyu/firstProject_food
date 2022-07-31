package com.yu;

import com.yu.service.DishService;
import com.yu.service.impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private DishService dishService;

    @Autowired
    private MailServiceImpl mailService;

    @Test
    void contextLoads() {
        String to = "zhouanyuxx@outlook.com";
        String subject = "Springboot 发送简单文本邮件";
        String content = "<h2>Hi~</h2><p>第一封 Springboot HTML 邮件</p>";
        mailService.sendSimpleTextMail(to ,content);
    }

}

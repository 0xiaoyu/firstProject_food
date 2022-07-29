package com.yu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    //localhost的跳转
    @GetMapping
    public String index(){
        System.out.println("1");
        return "<script>" +
                "window.location=\"backend/page/login/login.html\"" +
                "</script>";
    }
}

package com.yu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    //localhost的跳转
    @GetMapping("/1")
    public String indexBackend(){
        return "<script>" +
                "window.location=\"backend/page/login/login.html\"" +
                "</script>";
    }
    @GetMapping
    public String indexFront(){
        return "<script>" +
                "window.location=\"front/page/login.html\"" +
                "</script>";
    }
}

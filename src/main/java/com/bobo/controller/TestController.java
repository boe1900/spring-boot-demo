package com.bobo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 *
 * @author huabo
 * @date 2018/8/18
 */
@RestController
public class TestController {


    @GetMapping("/sayHello")
    public String sayHello() {
       return "hello";
    }



}

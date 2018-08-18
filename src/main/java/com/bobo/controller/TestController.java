package com.bobo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author huabo
 * @date 2018/8/18
 */
@RestController
public class TestController {


    @RequestMapping("/hello")
    public String sayHello(){
        return "hello jenkins..";
    }
}

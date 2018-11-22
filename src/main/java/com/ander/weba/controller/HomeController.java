package com.ander.weba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Date 2018/11/21 15:59.
 */


@Controller
@RequestMapping("/home")
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "commons/home";
    }

    @GetMapping("/demo")
    public String demo() {
        return "ue/demo";
    }
}

package com.ander.weba.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Date 2018/11/21 15:59.
 */


@Controller
public class IndexController {
    @RequestMapping(value = {"", "/login"})
    public String login(@RequestParam(required = false) String enName,
                        @RequestParam(required = false) String password,
                        @RequestParam(required = false) String captcha) {
        if (StringUtils.isBlank(enName) || StringUtils.isBlank(password) || StringUtils.isBlank(captcha)) {
            System.out.println("登录名或密码错误");
            return "login";
        } else {
            return "main";
        }
    }

}

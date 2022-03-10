package com.ajie.blog.account.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@RestController
@Controller
@RequestMapping("/micro-blog/v2/account")
public class TestController {

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "hello world";
    }

    @GetMapping("/redirect")
    public String redirect() {
        return "redirect:/micro-blog/v2/account/test";
    }
}

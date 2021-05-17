package com.ajie.blog.controller;

import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.mapper.BlogMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "测试入口")
@RestController
public class TestController {

    @Resource
    private BlogMapper blogMapper;

    @ApiOperation(value = "测试", notes = "测试入口")
    @GetMapping("test")
    public String test() {
        BlogPO tbBlog = blogMapper.selectById(1);
        UpdateWrapper wrap = new UpdateWrapper();
        wrap.eq(null, null);
        System.out.println(tbBlog);
        return "hello world";
    }

    @GetMapping("/actuator/info")
    public String eurekaTest() {
        return "micro-blog服务正常";
    }
}

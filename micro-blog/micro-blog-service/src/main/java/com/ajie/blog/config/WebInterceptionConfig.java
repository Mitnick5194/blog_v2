package com.ajie.blog.config;

import com.ajie.blog.interception.AuthInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebInterceptionConfig extends WebMvcConfigurerAdapter {
    @Resource
    private AuthInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自己的拦截器
        registry.addInterceptor(interceptor);
        super.addInterceptors(registry);
    }

}

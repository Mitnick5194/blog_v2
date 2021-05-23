package com.ajie.blog.account.config;

import com.ajie.blog.account.interception.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class WebInteceptionConfig extends WebMvcConfigurerAdapter {
    @Resource
    private AuthInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自己的拦截器
        registry.addInterceptor(interceptor);
        super.addInterceptors(registry);
    }
}

package com.ajie.blog.account.config;

import com.ajie.blog.account.interception.AuthInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

@Configuration
public class InteceptionConfig extends WebMvcConfigurerAdapter {
    @Resource
    private AuthInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自己的拦截器并设置拦截的请求路径
        registry.addInterceptor(interceptor).addPathPatterns("/**").
                excludePathPatterns("/micro-blog/v2/account/login", "**/hello",
                        "/micro-blog/v2/account/register");
        super.addInterceptors(registry);
    }

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}

package com.ajie.blog.account.config;

import com.ajie.commons.feign.FeignInterceptionConfig;
import com.ajie.commons.feign.InfoFeignLoggerFactory;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import feign.Logger;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Component
public class BeanConfig {

    /**
     * feign日志输出级别
     *
     * @return
     */
    @Bean
    Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 自定义feign日志工厂
     *
     * @return
     */
    @Bean
    FeignLoggerFactory infoFeignLoggerFactory() {
        return new InfoFeignLoggerFactory();
    }

    /**
     * feign拦截器
     *
     * @return
     */
    @Bean
    FeignInterceptionConfig getFeignInterceptionConfig() {
        return new FeignInterceptionConfig();
    }

    /**
     * mp分页拦截器
     *
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public InternalResourceViewResolver viewResolver(){
        return new InternalResourceViewResolver();
    }
}

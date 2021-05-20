package com.ajie.blog.config;

import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.stereotype.Component;

/**
 * feign日志打印级别默认是debug，重写feign日志工厂，使用info级别
 */
public  class InfoFeignLoggerFactory implements FeignLoggerFactory {
    @Override
    public Logger create(Class<?> type) {
        return new InfoFeignLogger(LoggerFactory.getLogger(type));
    }
}

package com.ajie.blog.config;

import feign.Feign;
import feign.Logger;

public class InfoFeignLogger extends Logger {

    private final org.slf4j.Logger  logger;

    public InfoFeignLogger(org.slf4j.Logger  logger) {
        this.logger = logger;
    }

    @Override
    protected void log(String s, String s1, Object... objects) {
        System.out.println(s);
    }
}

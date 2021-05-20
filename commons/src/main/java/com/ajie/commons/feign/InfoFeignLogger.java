package com.ajie.commons.feign;


import org.slf4j.Logger;

/**
 * 自定义feign调用日志输出
 */
public class InfoFeignLogger extends feign.Logger {

    private final Logger logger;

    public InfoFeignLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void log(String s, String s1, Object... objects) {
        logger.info(String.format(methodTag(s) + s1, objects));
    }
}

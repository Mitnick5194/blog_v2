package com.ajie.blog.account.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext cx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return cx;
    }
}

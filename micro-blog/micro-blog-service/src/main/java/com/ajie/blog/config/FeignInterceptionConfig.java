package com.ajie.blog.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptionConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.query("reqId", MDC.get("reqId"));
        requestTemplate.header("auth","test");
    }
}

package com.ajie.commons.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * feign调用添加全局的参数
 */
public class FeignInterceptionConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.query("reqId", MDC.get("reqId"));
    }
}

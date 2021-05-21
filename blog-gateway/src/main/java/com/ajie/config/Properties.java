package com.ajie.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置属性，可以使用propertyiesUtil获取
 */
@Component
@ConfigurationProperties(prefix = "properties")
public class Properties {
    public static String tokenSecret;

    public void setTokenSecret(String secret) {
        tokenSecret = secret;
    }

}

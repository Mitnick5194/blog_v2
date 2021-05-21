package com.ajie.blog.account.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置属性，可以使用propertyiesUtil获取
 */
@Component
@ConfigurationProperties(prefix = "properties")
@Getter
public class Properties {
    public static String tokenSecret;

    public void setTokenSecret(String secret) {
        tokenSecret = secret;
    }
}

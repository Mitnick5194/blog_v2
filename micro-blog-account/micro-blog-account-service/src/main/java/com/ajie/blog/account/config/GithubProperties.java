package com.ajie.blog.account.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * github授权登录配置
 *
 * @Author: niezhenjie
 * @Date: 2022/2/24
 */
@Component
@ConfigurationProperties(prefix = "properties.github")
@Data
@RefreshScope
public class GithubProperties {
    /**
     * 获取token链接
     */
    private String tokenUrl;
    /**
     * 获取用户信息链接
     */
    private String userInfoUrl;
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 客户端秘钥
     */
    private String clientSecret;
}

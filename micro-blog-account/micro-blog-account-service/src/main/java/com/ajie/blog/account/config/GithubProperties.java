package com.ajie.blog.account.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/24
 */
@Component
@ConfigurationProperties(prefix = "properties.github")
@Data
public class GithubProperties {
    private String oauthUrl;
    private String userInfoUrl;
    private String clientId;
    private String clientSecret;
}

package com.ajie.blog.account.api.dto;

import lombok.Data;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/26
 */
@Data
public class TokenDto {
    /**
     * 访问token
     */
    private String accessToken;
    /**
     * 刷新token
     */
    private String refreshToken;
}

package com.ajie.blog.account.service;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.api.dto.OAuthAccountDto;
import com.ajie.blog.account.exception.AccountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * oauth2验证登录
 *
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
public interface OAuthService {

    /**
     * 第三方授权登录
     *
     * @param code
     * @return
     */
    LoginRespDto oauthLogin(String code);

    /**
     * 获取第三方用户信息
     *
     * @param accessToken
     * @return
     */
    OAuthAccountDto getOauthUser(String accessToken);

    default  <T> T checkAndGetBody(ResponseEntity<T> response, int code, String msg) {
        HttpStatus statusCode = response.getStatusCode();
        if (HttpStatus.OK.value() != statusCode.value()) {
            throw new AccountException(code, msg);
        }
        return response.getBody();
    }


}

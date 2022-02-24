package com.ajie.blog.account.service;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.api.dto.OAuthAccountDto;

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


}

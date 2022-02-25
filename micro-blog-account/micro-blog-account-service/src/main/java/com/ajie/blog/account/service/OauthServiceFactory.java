package com.ajie.blog.account.service;

import com.ajie.commons.enums.OauthType;

/**
 * 认证服务工厂
 */
public interface OauthServiceFactory {
    /**
     * 注册工厂
     *
     * @param service
     * @param type
     * @return
     */
    boolean register(OAuthService service, OauthType type);


    /**
     * 获取服务
     *
     * @param type
     * @return
     */
    OAuthService getService(OauthType type);
}

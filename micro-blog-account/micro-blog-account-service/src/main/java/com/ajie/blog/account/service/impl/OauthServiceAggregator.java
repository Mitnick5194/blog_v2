package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.service.OAuthService;
import com.ajie.blog.account.service.OauthServiceFactory;
import com.ajie.commons.enums.OauthType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/25
 */
@Service
public class OauthServiceAggregator implements OauthServiceFactory {

    /**
     * 服务
     */
    private Map<Integer, OAuthService> serviceMap;

    public OauthServiceAggregator() {
        serviceMap = new HashMap<>(8);
    }

    @Override
    public boolean register(OAuthService service, OauthType type) {
        OAuthService oAuthService = serviceMap.get(type.getCode());
        if (null != oAuthService) {
            return false;
        }
        serviceMap.put(type.getCode(), service);
        return true;
    }

    @Override
    public OAuthService getService(OauthType type) {
        return serviceMap.get(type.getCode());
    }
}

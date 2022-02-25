package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.OAuthAccountDto;
import com.ajie.blog.account.api.dto.TokenDto;
import com.ajie.blog.account.config.GiteeProperties;
import com.ajie.commons.enums.OauthType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/24
 */
@Service
@Slf4j
public class GiteeOauthServiceImpl extends AbstractOauthServiceImpl {
    @Resource
    private GiteeProperties giteeProperties;

    @Override
    public OAuthAccountDto getOauthUserByToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/json");
        Map<String, String> map = new HashMap<>(0);
        HttpEntity<?> entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(giteeProperties.getUserInfoUrl(), HttpMethod.GET, entity, String.class);
        String body = checkAndGetBody(response, HttpStatus.UNAUTHORIZED.value(), "授权信息失效");
        JSONObject json = JSON.parseObject(body);
        String id = json.getString("id");
        if (StringUtils.isBlank(id)) {
            return null;
        }
        OAuthAccountDto account = new OAuthAccountDto();
        account.setOauthId(id);
        account.setHeaderUrl(json.getString("avatar_url"));
        account.setAccessToken(accessToken);
        String name = json.getString("name");
        if (StringUtils.isBlank(name)) {
            name = json.getString("login");
        }
        account.setNickName(name);
        account.setOauthName(name);
        account.setMail(json.getString("email"));
        return account;
    }

    @Override
    protected TokenDto getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Accept", "application/json");
        Map<String, String> params = new HashMap<>(4);
        params.put("grant_type", "authorization_code");
        params.put("client_id", giteeProperties.getClientId());
        params.put("client_secret", giteeProperties.getClientSecret());
        params.put("code", code);
        params.put("redirect_uri", giteeProperties.getRedirectUrl());
        HttpEntity<Map<String, String>> entity = new HttpEntity(params, headers);
        log.info("gitee登录授权，入参：{}", code);
        ResponseEntity<String> response = restTemplate.postForEntity(giteeProperties.getTokenUrl(), entity, String.class);
        String body = checkAndGetBody(response, HttpStatus.UNAUTHORIZED.value(), "登录失败");
        log.info("gitee登录授权，响应：{}", body);
        JSONObject json = JSON.parseObject(body);
        String accessToken = json.getString("access_token");
        String refreshToken = json.getString("refresh_token");
        TokenDto token = new TokenDto();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        return token;
    }

    @Override
    protected OauthType getOauthType() {
        return OauthType.GITEE;
    }
}

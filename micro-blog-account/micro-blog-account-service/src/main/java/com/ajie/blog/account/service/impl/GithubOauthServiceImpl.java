package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.OAuthAccountDto;
import com.ajie.blog.account.api.dto.TokenDto;
import com.ajie.blog.account.config.GithubProperties;
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
 * github授权
 *
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
@Service
@Slf4j
public class GithubOauthServiceImpl extends AbstractOauthServiceImpl {
    @Resource
    private GithubProperties githubProperties;

    @Override
    protected TokenDto getToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Accept", "application/json");
        Map<String, String> map = new HashMap<>(4);
        map.put("client_id", githubProperties.getClientId());
        map.put("client_secret", githubProperties.getClientSecret());
        map.put("code", code);
        HttpEntity<Map<String, String>> entity = new HttpEntity(map, headers);
        log.info("github登录授权，入参：{}", code);
        ResponseEntity<String> response = restTemplate.postForEntity(githubProperties.getTokenUrl(), entity, String.class);
        String body = checkAndGetBody(response, HttpStatus.UNAUTHORIZED.value(), "登录失败");
        log.info("github登录授权，响应：{}", body);
        JSONObject json = JSON.parseObject(body);
        String accessToken = json.getString("access_token");
        TokenDto token = new TokenDto();
        token.setAccessToken(accessToken);
        return token;
    }

    @Override
    protected OauthType getOauthType() {
        return OauthType.GITHUB;
    }


    @Override
    public OAuthAccountDto getOauthUserByToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Authorization", "token " + accessToken);
        headers.add("Accept", "application/json");
        Map<String, String> map = new HashMap<>(0);
        HttpEntity<?> entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(githubProperties.getUserInfoUrl(), HttpMethod.GET, entity, String.class);
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
}

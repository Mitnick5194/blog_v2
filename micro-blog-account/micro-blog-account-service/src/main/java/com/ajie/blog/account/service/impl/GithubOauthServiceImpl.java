package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.api.dto.OAuthAccountDto;
import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.api.po.OauthAccountPO;
import com.ajie.blog.account.config.GithubProperties;
import com.ajie.blog.account.config.Properties;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.blog.account.mapper.OauthMapper;
import com.ajie.blog.account.service.OAuthService;
import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.enums.OauthType;
import com.ajie.commons.utils.JwtUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
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
public class GithubOauthServiceImpl implements OAuthService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private OauthMapper oauthMapper;
    @Resource
    private GithubProperties githubProperties;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginRespDto oauthLogin(String code) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        headers.add("Accept", "application/json");
        Map<String, String> map = new HashMap<>(4);
        map.put("client_id", githubProperties.getClientId());
        map.put("client_secret", githubProperties.getClientSecret());
        map.put("code", code);
        HttpEntity<Map<String, String>> entity = new HttpEntity(map, headers);
        log.info("github登录授权，入参：{}", code);
        ResponseEntity<String> response = restTemplate.postForEntity(githubProperties.getOauthUrl(), entity, String.class);
        String body = checkAndGetBody(response, HttpStatus.UNAUTHORIZED.value(), "登录失败");
        log.info("github登录授权，响应：{}", body);
        JSONObject json = JSON.parseObject(body);
        String accessToken = json.getString("access_token");
        OAuthAccountDto oauthUser = getOauthUser(accessToken);
        if (null == oauthUser) {
            throw new AccountException(HttpStatus.UNAUTHORIZED.value(), "登录失败");
        }
        OauthAccountPO account = new OauthAccountPO();
        account.setOauthId(oauthUser.getOauthId());
        account.setOauthType(OauthType.GITHUB.getCode());
        OauthAccountPO oauthAccountPO = oauthMapper.selectOne(account.wrap(OauthAccountPO.class));
        if (null != oauthAccountPO) {
            //不是第一次授权
            //更新一下信息
            updateOauthAccount(oauthUser);
            AccountPO accountPO = accountMapper.selectById(oauthAccountPO.getAccountId());
            return buildRespAccount(accountPO);
        }
        AccountPO accountPO = registerOauthAccount(oauthUser);
        return buildRespAccount(accountPO);
    }

    private LoginRespDto buildRespAccount(AccountPO account) {
        JwtAccount jwtAccount = new JwtAccount();
        BeanUtils.copyProperties(account, jwtAccount);
        String token = JwtUtil.createToken(Properties.tokenSecret, jwtAccount);
        LoginRespDto resp = new LoginRespDto();
        BeanUtils.copyProperties(account, resp);
        //第三方认证不适用accountName，因为第三方系统的用户名可能会重复（github的用户名和微博的用户名重复一点都不奇怪~~~~~）
        resp.setAccountName(account.getNickName());
        resp.setNickName(account.getNickName());
        resp.setPhone(AccountHelper.mask(account.getPhone()));
        resp.setMail(AccountHelper.mask(account.getMail()));
        resp.setToken(token);
        return resp;
    }

    private AccountPO registerOauthAccount(OAuthAccountDto oAuthAccountDto) {
        //插入本地用户表
        AccountPO po = new AccountPO();
        BeanUtils.copyProperties(oAuthAccountDto, po);
        accountMapper.insert(po);
        //插入第三方授权用户表
        OauthAccountPO oauthAccountPO = new OauthAccountPO();
        BeanUtils.copyProperties(oAuthAccountDto, oauthAccountPO);
        oauthAccountPO.setAccountId(po.getId());
        oauthAccountPO.setOauthType(OauthType.GITHUB.getCode());
        oauthMapper.insert(oauthAccountPO);
        return po;
    }

    private void updateOauthAccount(OAuthAccountDto oauthAccountDto) {
        OauthAccountPO po = new OauthAccountPO();
        po.setOauthId(oauthAccountDto.getOauthId());
        OauthAccountPO oauthAccountPO = oauthMapper.selectOne(po.wrap(OauthAccountPO.class));
        oauthAccountPO.setOauthName(oauthAccountDto.getOauthName());
        oauthAccountPO.setAccessToken(oauthAccountDto.getAccessToken());
        oauthAccountPO.setRefreshToken(oauthAccountDto.getRefreshToken());
        oauthAccountPO.setUpdateTime(new Date());
        oauthMapper.updateById(oauthAccountPO);
        //更新用户表
        Long accountId = oauthAccountPO.getAccountId();
        AccountPO accountPO = accountMapper.selectById(accountId);
        accountPO.setNickName(oauthAccountDto.getNickName());
        accountPO.setMail(oauthAccountDto.getMail());
        accountPO.setHeaderUrl(oauthAccountDto.getHeaderUrl());
        accountMapper.updateById(accountPO);
    }


    @Override
    public OAuthAccountDto getOauthUser(String accessToken) {
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

    private <T> T checkAndGetBody(ResponseEntity<T> response, int code, String msg) {
        HttpStatus statusCode = response.getStatusCode();
        if (HttpStatus.OK.value() != statusCode.value()) {
            throw new AccountException(code, msg);
        }
        return response.getBody();
    }
}

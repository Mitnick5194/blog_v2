package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.api.dto.OAuthAccountDto;
import com.ajie.blog.account.api.dto.TokenDto;
import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.api.po.OauthAccountPO;
import com.ajie.blog.account.config.Properties;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.blog.account.mapper.OauthMapper;
import com.ajie.blog.account.service.OAuthService;
import com.ajie.blog.account.service.OauthServiceFactory;
import com.ajie.blog.account.service.RegisterOauthService;
import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.enums.OauthType;
import com.ajie.commons.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * 第三方授权登录基础实现
 *
 * @Author: niezhenjie
 * @Date: 2022/2/26
 */
public abstract class AbstractOauthServiceImpl implements OAuthService, RegisterOauthService {
    /**
     * 失败重试次数
     */
    protected final int RETRY_COUNT = 3;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private OauthMapper oauthMapper;
    @Resource
    private OauthServiceFactory oauthServiceFactory;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginRespDto oauthLogin(String code) {
        TokenDto token = getToken(code);
        OAuthAccountDto oauthUser = getOauthUserByToken(token.getAccessToken());
        if (null == oauthUser) {
            throw new AccountException(HttpStatus.UNAUTHORIZED.value(), "登录失败");
        }
        oauthUser.setRefreshToken(oauthUser.getRefreshToken());
        OauthAccountPO account = new OauthAccountPO();
        account.setOauthId(oauthUser.getOauthId());
        account.setOauthType(getOauthType().getCode());
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

    @PostConstruct
    @Override
    public boolean register() {
        return oauthServiceFactory.register(this, getOauthType());
    }

    private LoginRespDto buildRespAccount(AccountPO account) {
        JwtAccount jwtAccount = new JwtAccount();
        BeanUtils.copyProperties(account, jwtAccount);
        String token = JwtUtil.createToken(Properties.tokenSecret, jwtAccount);
        LoginRespDto resp = new LoginRespDto();
        BeanUtils.copyProperties(account, resp);
        //第三方认证不适用accountName，因为第三方系统的用户名可能会重复（gitee的用户名和微博的用户名重复一点都不奇怪~~~~~）
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
        oauthAccountPO.setOauthType(getOauthType().getCode());
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

    public <T> T checkAndGetBody(ResponseEntity<T> response, int code, String msg) {
        HttpStatus statusCode = response.getStatusCode();
        if (HttpStatus.OK.value() != statusCode.value()) {
            throw new AccountException(code, msg);
        }
        return response.getBody();
    }

    /**
     * 获取token
     *
     * @return
     */
    protected abstract TokenDto getToken(String code);

    protected abstract OauthType getOauthType();


}

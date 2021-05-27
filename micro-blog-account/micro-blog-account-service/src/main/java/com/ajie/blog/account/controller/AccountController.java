package com.ajie.blog.account.controller;

import com.ajie.blog.account.api.dto.*;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.service.AccountService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.exception.MicroCommonException;
import com.ajie.commons.utils.RandomUtil;
import com.ajie.commons.utils.VerifyCodeUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/micro-blog/v2/account")
public class AccountController implements AccountRestApi {
    @Resource
    private AccountService accountService;


    @Override
    public RestResponse<Integer> register(RegisterReqDto dto) {
        return RestResponse.success(accountService.register(dto));
    }

    @Override
    public RestResponse<LoginRespDto> login(LoginReqDto dto) {
        return RestResponse.success(accountService.login(dto));
    }

    @Override
    public RestResponse<Integer> logout() {
        return RestResponse.success(accountService.logout());
    }

    @Override
    public RestResponse<Integer> changePassword(ChangePasswordReqDto dto) {
        return RestResponse.success(accountService.changePassword(dto));
    }

    @Override
    public RestResponse<Integer> updateUserInfo(UpdateUserReqDto dto) {
        return RestResponse.success(accountService.updateUserInfo(dto));
    }

    @Override
    public RestResponse<Integer> updateAccountName(UpdateAccountNameReqDto dto) {
        return RestResponse.success(accountService.updateAccountName(dto));
    }

    @Override
    public RestResponse<List<AccountRespDto>> queryAccountInfo(List<Long> ids) {
        return RestResponse.success(accountService.queryAccountInfo(ids));
    }

    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @GetMapping("/get-verify-code")
    public RestResponse<VerifyCodeRestDto> getVerifyCode() {
        return RestResponse.success(accountService.getVerifyCode());
    }
}

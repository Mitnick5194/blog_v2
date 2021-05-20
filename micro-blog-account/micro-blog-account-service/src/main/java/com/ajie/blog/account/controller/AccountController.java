package com.ajie.blog.account.controller;

import com.ajie.blog.account.api.dto.*;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.account.service.AccountService;
import com.ajie.commons.RestResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public RestResponse<String> login(LoginReqDto dto) {
        return RestResponse.success(accountService.login(dto));
    }

    @Override
    public RestResponse<Integer> loginout() {
        return RestResponse.success(accountService.loginout());
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
    public RestResponse<Integer> updateAccountName(String accountName) {
        return RestResponse.success(accountService.updateAccountName(accountName));
    }
}

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/micro-blog/v2/account")
public class AccountController implements AccountRestApi {
    @Resource
    private AccountService accountService;

    @Override
    public RestResponse<Integer> register(RegisterReqDto dto) {
        String key = dto.getKey();
        String verifyCode = dto.getVerifyCode();
        String s = verifyCodeMap.get(key);
        if (null == s || !s.equalsIgnoreCase(verifyCode)) {
            throw new AccountException(MicroCommonException.PARAM_ERROR.getCode(), "验证码错误");
        }
        return RestResponse.success(accountService.register(dto));
    }

    @Override
    public RestResponse<LoginRespDto> login(LoginReqDto dto) {
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
    public RestResponse<Integer> updateAccountName(UpdateAccountNameReqDto dto) {
        return RestResponse.success(accountService.updateAccountName(dto));
    }

    @Override
    public RestResponse<List<AccountRespDto>> queryAccountInfo(List<Long> ids) {
        return RestResponse.success(accountService.queryAccountInfo(ids));
    }

    //TODO 改成redis
    private Map<String, String> verifyCodeMap = new HashMap<>();

    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @GetMapping("/get-verify-code")
    public RestResponse<VerifyCodeRestDto> getVerifyCode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String code = VerifyCodeUtil.drawImage(out);
        //转成base64
        String encodeCode = Base64.encodeBase64String(out.toByteArray());
        String key = RandomUtil.getRandomString_36();
        verifyCodeMap.put(key, code);
        VerifyCodeRestDto dto = new VerifyCodeRestDto();
        dto.setKey(key);
        dto.setVerifyCode(encodeCode);
        return RestResponse.success(dto);
    }
}

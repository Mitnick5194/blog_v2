package com.ajie.blog.account.controller;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.service.OAuthService;
import com.ajie.commons.RestResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/22
 */
@RestController
@RequestMapping("/micro-blog/v2/account/oauth")
public class OAuthController {
    @Resource
    private OAuthService oAuthService;

    @GetMapping("/github")
    public RestResponse<LoginRespDto> github(@RequestParam("code") @NotNull(message = "code不能为空") String code) throws IOException {
        return RestResponse.success(oAuthService.oauthLogin(code));
    }
}

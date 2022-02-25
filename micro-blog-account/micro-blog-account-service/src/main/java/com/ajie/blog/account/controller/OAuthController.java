package com.ajie.blog.account.controller;

import com.ajie.blog.account.api.dto.LoginRespDto;
import com.ajie.blog.account.service.OauthServiceFactory;
import com.ajie.commons.RestResponse;
import com.ajie.commons.enums.OauthType;
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
@CrossOrigin
public class OAuthController {
    @Resource
    private OauthServiceFactory oauthServiceFactory;

    @GetMapping("/github")
    public RestResponse<LoginRespDto> github(@RequestParam("code") @NotNull(message = "code不能为空") String code) throws IOException {
        return RestResponse.success(oauthServiceFactory.getService(OauthType.GITHUB).oauthLogin(code));
    }

    @GetMapping("/gitee")
    public RestResponse<LoginRespDto> gitee(@RequestParam("code") @NotNull(message = "code不能为空") String code) throws IOException {
        return RestResponse.success(oauthServiceFactory.getService(OauthType.GITEE).oauthLogin(code));
    }
}

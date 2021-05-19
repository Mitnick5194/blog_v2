package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "LoginReqDto", description = "用户登录")
@Getter
@Setter
public class LoginReqDto extends BaseReqDto {
    @ApiModelProperty(name = "accountName", value = "用户（用户名、邮箱、手机号）")
    private String user;
    @ApiModelProperty(name = "password", value = "密码")
    private String password;
}

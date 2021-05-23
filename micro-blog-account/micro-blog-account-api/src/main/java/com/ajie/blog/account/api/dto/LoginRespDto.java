package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseRespDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 登录成功返回
 */
@Setter
@Getter
@ApiModel(value = "LoginRespDto", description = "登录用户信息")
public class LoginRespDto extends AccountRespDto {
    /**
     * 用户凭证
     */
    @ApiModelProperty(value = "用户凭证")
    private String token;
}

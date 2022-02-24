package com.ajie.blog.account.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 第三方认证用户
 *
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
@Data
public class OAuthAccountDto extends AccountDto {
    @ApiModelProperty(value = "第三方用户表主键")
    private Long id;
    @ApiModelProperty(value = "account表ID")
    private Long accountId;
    @ApiModelProperty(value = "第三方系统的ID")
    private String oauthId;
    @ApiModelProperty(value = "授权系统用户名称，与mb_account的名称区分开，此名称可重复且不能用于登录")
    private String oauthName;
    @ApiModelProperty(value = "授权系统类型，1：github")
    private Integer oauthType;
    @ApiModelProperty(value = "访问token")
    private String accessToken;
    @ApiModelProperty(value = "刷新token")
    private String refreshToken;
}

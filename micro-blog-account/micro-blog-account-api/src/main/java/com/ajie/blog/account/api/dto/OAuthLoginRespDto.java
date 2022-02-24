package com.ajie.blog.account.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
@Data
public class OAuthLoginRespDto extends LoginRespDto {
    /**
     * 刷新token
     */
    @ApiModelProperty(value = "刷新token")
    private String refreshToken;
}

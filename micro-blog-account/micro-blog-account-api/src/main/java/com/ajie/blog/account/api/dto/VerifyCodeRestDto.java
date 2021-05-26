package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseRespDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 验证码
 */
@Getter
@Setter
@ApiModel(value = "VerifyCodeRestDto", description = "验证码")
public class VerifyCodeRestDto extends BaseRespDto {
    @ApiModelProperty(value = "验证码key")
    private String key;
    @ApiModelProperty(value = "验证码base64输出")
    private String verifyCode;
}


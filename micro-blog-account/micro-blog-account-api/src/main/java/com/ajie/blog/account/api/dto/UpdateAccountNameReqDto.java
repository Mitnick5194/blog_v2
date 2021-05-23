package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新用户名
 */
@ApiModel(value = "UpdateAccountNameReqDto", description = "更新用户名")
@Getter
@Setter
public class UpdateAccountNameReqDto extends BaseReqDto {
    /**
     * 密码验证
     */
    @ApiModelProperty(value = "密码验证")
    private String password;
    /**
     * 新用户名
     */
    @ApiModelProperty(value = "新用户名")
    private String accountName;
}

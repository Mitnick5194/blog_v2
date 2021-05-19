package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改密码
 */
@ApiModel(value = "ChangePasswordReqDto", description = "修改密码")
@Getter
@Setter
public class ChangePasswordReqDto extends BaseReqDto {

    @ApiModelProperty(value = "原密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    private String newPassword;
}

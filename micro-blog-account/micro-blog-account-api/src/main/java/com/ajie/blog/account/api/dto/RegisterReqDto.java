package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "RegisterReqDto", description = "用户注册")
@Getter
@Setter
public class RegisterReqDto extends UpdateUserReqDto {
    @ApiModelProperty(name = "accountName", value = "用户名")
    private String accountName;
    @ApiModelProperty(name = "password", value = "密码")
    private String password;

}

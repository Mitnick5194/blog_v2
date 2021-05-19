package com.ajie.blog.account.api.dto;

import com.ajie.commons.dto.BaseRespDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "AccountRespDto", description = "用户信息")
@Getter
@Setter
public class AccountRespDto extends BaseRespDto {
    @ApiModelProperty(name = "accountName", value = "用户名")
    private String accountName;
    @ApiModelProperty(name = "nickName", value = "昵称")
    private String nickName;
    @ApiModelProperty(name = "mail", value = "脱敏邮箱")
    private String mail;
    @ApiModelProperty(name = "phone", value = "脱敏手机号")
    private String phone;
    @ApiModelProperty(name = "gender", value = "性别，1男2女3保密")
    private Integer gender;
    @ApiModelProperty(name = "personalSign", value = "个性签名")
    private String personalSign;

    @ApiModelProperty(name = "headerUrl", value = "头像地址")
    private String headerUrl;
}

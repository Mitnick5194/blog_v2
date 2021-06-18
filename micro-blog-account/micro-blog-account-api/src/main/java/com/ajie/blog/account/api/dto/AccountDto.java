package com.ajie.blog.account.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "AccountDto", description = "AccountDto")
@Getter
@Setter
public class AccountDto implements Serializable {
    /**
     * 用户名
     */
    @ApiModelProperty(name = "accountName", value = "用户名")
    private String accountName;
    /**
     * 昵称
     */
    @ApiModelProperty(name = "nickName", value = "昵称")
    private String nickName;
    /* *//**
     * 密码
     *//*
    @ApiModelProperty(name = "password", value = "密码")
    private String password;
    *//**
     * 邮箱
     *//*
    @ApiModelProperty(name = "mail", value = "邮箱")
    private String mail;
    *//**
     * 手机号
     *//*
    @ApiModelProperty(name = "phone", value = "手机号")
    private String phone;*/
    /**
     * 性别，1男2女3保密
     */
    @ApiModelProperty(name = "gender", value = "性别，1男2女3保密")
    private Integer gender;
    /**
     * 个性签名
     */
    @ApiModelProperty(name = "personalSign", value = "个性签名")
    private String personalSign;
    /**
     * 头像地址
     */
    @ApiModelProperty(name = "headerUrl", value = "头像地址")
    private String headerUrl;

}
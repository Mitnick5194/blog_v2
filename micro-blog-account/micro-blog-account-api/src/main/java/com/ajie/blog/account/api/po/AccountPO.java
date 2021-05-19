package com.ajie.blog.account.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@TableName("mb_account")
@Getter
@Setter
public class AccountPO extends BasePO {
    /**
     * 用户名
     */

    private String accountName;
    /**
     * 昵称
     */

    private String nickName;
    /**
     * 密码
     */

    private String password;
    /**
     * 邮箱
     */

    private String mail;
    /**
     * 手机号
     */

    private String phone;
    /**
     * 性别，1男2女3保密
     */

    private Integer gender;
    /**
     * 个性签名
     */

    private String personalSign;
    /**
     * 头像地址
     */

    private String headerUrl;
}
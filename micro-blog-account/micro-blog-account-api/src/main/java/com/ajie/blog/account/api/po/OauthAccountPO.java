package com.ajie.blog.account.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
@TableName("mb_oauth_account")
@Getter
@Setter
public class OauthAccountPO extends BasePO {
    /**
     * 用户ID(对应mb_account主键)
     */
    private Long accountId;
    /**
     * 授权系统用户ID
     */
    private String oauthId;
    /**
     * 授权系统用户名称，与mb_account的名称区分开，此名称可重复且不能用于登录
     */
    private String oauthName;
    /**
     * 授权系统类型，1：github
     */
    private Integer oauthType;
    /**
     * 访问token
     */
    private String accessToken;
    /**
     * 刷新token
     */
    private String refreshToken;
}

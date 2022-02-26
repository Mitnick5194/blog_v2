package com.ajie.gateway.dto;

import com.alibaba.fastjson.JSON;

import java.util.Date;

public class JwtAccount {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String accountName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像链接
     */
    private String headerUrl;

    /**
     * 附加信息，json格式
     */
    private String attach;

    /**
     * 签名，禁序列化
     */
    transient private String sign;

    /**
     * 过期信息，禁序列化
     */
    transient private Date expire;

    public <T> T getAttachObject(Class<T> clazz) {
        if (null == attach || attach.trim().length() == 0) {
            return (T) null;
        }
        return JSON.parseObject(attach, clazz);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }
}

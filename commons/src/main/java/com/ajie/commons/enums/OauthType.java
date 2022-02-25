package com.ajie.commons.enums;

/**
 * 授权类型
 *
 * @Author: niezhenjie
 * @Date: 2022/2/23
 */
public enum OauthType {
    GITHUB(1, "github"),

    GITEE(2, "gitee");

    private int code;
    private String msg;

    private OauthType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

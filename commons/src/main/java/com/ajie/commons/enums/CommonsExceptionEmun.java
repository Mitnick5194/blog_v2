package com.ajie.commons.enums;

/**
 * 通用异常
 */
public enum CommonsExceptionEmun {

    PARAM_ERROR(1001, "参数异常"),

    /**
     * 属主判断，删除和修改的时候需要判断当前登录这是否为数据的拥有着，防止篡改
     */
    BELONG_NOT_MATCH(1002, "属主异常");

    private int code;
    private String msg;

    private CommonsExceptionEmun(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}

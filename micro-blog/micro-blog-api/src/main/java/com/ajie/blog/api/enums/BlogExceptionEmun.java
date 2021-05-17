package com.ajie.blog.api.enums;

/**
 * 博客异常
 */
public enum BlogExceptionEmun {

    PARAM_ERROR(1001, "参数异常"),

    /**
     * 删改需匹配当前登录用户和操作的数据所属用户是否同一个人，在过滤器统一拦截判断
     */
    BELONG_NOT_MATCH(1002, "属主异常");

    private int code;
    private String msg;

    private BlogExceptionEmun(int code, String msg) {
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

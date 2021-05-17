package com.ajie.commons.exception;

/**
 * 通用异常
 */
public class CommonException extends RuntimeException {
    /**
     * 服务器异常code
     */
    public static final int SERVICE_ERROR_CODE = 500;
    public static final String SERVICE_ERROR_MSG = "服务器异常";
    private Integer code;
    private String msg;


    public CommonException(Integer code, String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public CommonException(Throwable e) {
        this(SERVICE_ERROR_CODE, SERVICE_ERROR_MSG, e);
    }

    public CommonException(Integer code, String msg) {
        this(code, msg, null);
    }

    public static CommonException of(Integer code, String msg) {
        CommonException e = new CommonException(code, msg);
        return e;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

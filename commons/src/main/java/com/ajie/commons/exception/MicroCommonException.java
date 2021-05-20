package com.ajie.commons.exception;

import org.apache.commons.lang3.StringUtils;

public class MicroCommonException extends CommonException {
    public static final MicroCommonException PARAM_ERROR = new MicroCommonException(5001, "参数异常");
    /**
     * 删改需匹配当前登录用户和操作的数据所属用户是否同一个人
     */
    public static final MicroCommonException BELONG_NOT_MATCH = new MicroCommonException(5002, "属主异常");

    /**
     * 用户校验异常
     */
    public static final MicroCommonException USER_VERIFY_ERROR = new MicroCommonException(5003, "用户校验异常");

    /**
     * 用户登录过期
     */
    public static final MicroCommonException USER_LOGIN_EXPIRE = new MicroCommonException(401, "会话过期，请重新登录");


    public MicroCommonException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public MicroCommonException(Throwable e) {
        super(e);
    }

    public MicroCommonException(int code, String msg) {
        super(code, msg);
    }

    public MicroCommonException paramErrorFiled(String field) {
        if (StringUtils.isNotBlank(field)) {
            String msg = getMsg();
            setMsg(new StringBuffer().append(msg).append(":").append(field).toString());
        }
        return this;
    }
}

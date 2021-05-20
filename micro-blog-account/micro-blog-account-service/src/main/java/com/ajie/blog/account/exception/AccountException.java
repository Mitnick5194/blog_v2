package com.ajie.blog.account.exception;

import com.ajie.commons.exception.MicroCommonException;

public class AccountException extends MicroCommonException {

    public static final AccountException USER_NAME_EXIST = new AccountException(1003, "用户名已存在");

    public static final AccountException USER_REGISTER_FAIL = new AccountException(1004, "注册失败，请重新提交");

    public static final AccountException USER_ILLEGAL_NAME = new AccountException(1005, "非法用户名，用户名只能包含英文数字和'-''_'，且不能纯数字");

    public static final AccountException LOGIN_FAIL = new AccountException(1006, "登录失败，用户名或密码错误");

    public AccountException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public AccountException(Throwable e) {
        super(e);
    }

    public AccountException(int code, String msg) {
        super(code, msg);
    }
}

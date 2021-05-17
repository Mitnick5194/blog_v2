package com.ajie.blog.exception;

import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.commons.exception.CommonException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class BlogException extends CommonException {

    public BlogException(BlogExceptionEmun e) {
        this(e.getCode(), e.getMsg());
    }

    public BlogException(int code, String msg, Throwable e) {
        super(code, msg, e);
    }

    public BlogException(Throwable e) {
        super(e);
    }

    public BlogException(int code, String msg) {
        super(code, msg);
    }

    public static BlogException of(BlogExceptionEmun e) {
        return new BlogException(e);
    }

    public static BlogException paramError(String field) {
        BlogException of = of(BlogExceptionEmun.PARAM_ERROR);
        if (StringUtils.isNotBlank(field)) {
            String msg = of.getMsg();
            of.setMsg(new StringBuffer().append(msg).append(":").append(field).toString());
        }
        return of;
    }
}

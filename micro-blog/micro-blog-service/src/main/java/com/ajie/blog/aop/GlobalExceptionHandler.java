package com.ajie.blog.aop;


import com.ajie.commons.RestResponse;
import com.ajie.commons.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 统一异常返回处理器
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public RestResponse globalException(HttpServletResponse response, Throwable e) {
        logger.error("", e);
        if (e instanceof CommonException) {
            CommonException ex = (CommonException) e;
            int code = ex.getCode();
            String message = ex.getMsg();
            if (null == message) {
                message = CommonException.SERVICE_ERROR_MSG;
            }
            if (0 == code) {
                code = CommonException.SERVICE_ERROR_CODE;
            }
            return RestResponse.fail(code, message);
        }
        return RestResponse.fail(CommonException.SERVICE_ERROR_CODE,
                e.getMessage() == null ? CommonException.SERVICE_ERROR_MSG : e.getMessage());
    }
}

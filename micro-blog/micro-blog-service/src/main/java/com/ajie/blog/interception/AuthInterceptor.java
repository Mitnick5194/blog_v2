package com.ajie.blog.interception;

import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.exception.BlogException;
import com.ajie.chilli.utils.common.JsonUtils;
import com.ajie.commons.RestResponse;
import com.ajie.commons.utils.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";
    /**
     * 网关解析后的用户信息
     */
    private static final String TICKET_KEY = "ticket";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqId = request.getParameter("reqId");
        if (StringUtils.isBlank(reqId)) {
            reqId = RandomUtil.getRandomString(12, true);
        }
        //日志追踪id
        MDC.put("reqId", reqId);
        String requestURI = request.getRequestURI();

        return true;
    }
}

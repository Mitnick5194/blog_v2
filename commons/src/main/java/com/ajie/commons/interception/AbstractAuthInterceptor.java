package com.ajie.commons.interception;

import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.feign.FeignInterceptionConfig;
import com.ajie.commons.utils.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAuthInterceptor extends HandlerInterceptorAdapter {
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";
    /**
     * 网关解析后的用户信息
     */
    public static final String TICKET_KEY = "ticket";

    /**
     * 测试头部，用于api调试，无需认证和判断权限
     */
    public static final String TEST_HEADER = "test";
    /**
     * feign调用时识别是否为api测试
     */
    public static final String AUTH_TEST_KEY = "auth_test";


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //String reqId = request.getParameter("reqId");
        //改成放入头部
        String reqId = request.getHeader("reqId");
        if (StringUtils.isBlank(reqId)) {
            reqId = RandomUtil.getRandomString(12, true);
        }
        //日志追踪id
        MDC.put("reqId", reqId);
        String token = request.getHeader(TICKET_KEY);
        parseTicket(token);
        //验证路径是否要登录
        if (assertAuth(request)) { //一般都是true，只是为了系统更加强壮和可扩展，再做一次判断（因为网关已经校验过是否需要登录了）
            return true;
        }
        if (StringUtils.isBlank(token)) {
            return writer(response, LOGINRESP);
        }
        if (TEST_HEADER.equals(token) && ServerUtil.isDev()) {
            //feign调用时识别是否为api测试
            UserInfoUtil.setUserName(AUTH_TEST_KEY);
            return true;
        }
        return true;
    }

    /**
     * 是否验证登录
     *
     * @return
     */
    protected abstract boolean assertAuth(HttpServletRequest request);

    private boolean writer(HttpServletResponse response, String msg) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            out.append(msg);
            out.flush();
        } finally {
            out.close();
        }
        return false;
    }

    private JwtAccount parseTicket(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        JwtAccount account = JSON.parseObject(token, JwtAccount.class);
        UserInfoUtil.setUserId(account.getId());
        UserInfoUtil.setUserHeader(account.getHeaderUrl());
        UserInfoUtil.setUserName(account.getAccountName());
        return account;
    }


}

package com.ajie.commons.interception;

import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.utils.JwtUtil;
import com.ajie.commons.utils.RandomUtil;
import com.ajie.commons.utils.UserInfoUtil;
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
        String token = request.getHeader("ticket");
        if (StringUtils.isBlank(token)) {
            return writer(response, LOGINRESP);
        }
        JwtAccount account = JSON.parseObject(token, JwtAccount.class);
        UserInfoUtil.setUserId(account.getId());
        UserInfoUtil.setUserHeader(account.getHeaderUrl());
        UserInfoUtil.setUserName(account.getAccountName());
        return true;
    }

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
}

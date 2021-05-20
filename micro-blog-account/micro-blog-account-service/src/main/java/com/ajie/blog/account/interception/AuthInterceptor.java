package com.ajie.blog.account.interception;

import com.ajie.blog.account.config.Properties;
import com.ajie.blog.account.config.PropertiesUtil;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.utils.JwtUtil;
import com.ajie.commons.utils.RandomUtil;
import com.ajie.commons.utils.UserInfoUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";
    //测试头部，用于api调试，无需认证和判断权限
    private static final String testHeader = "test";
/*
    @Resource
    private Properties properties;*/


    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String testAuth = request.getHeader("testAuth");
        String reqId = request.getParameter("reqId");
        if (StringUtils.isBlank(reqId)) {
            reqId = RandomUtil.getRandomString(12, true);
        }
        //日志追踪id
        MDC.put("reqId", reqId);
        if (testHeader.equals(testAuth)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        //TODO 判断登录
       /* String token = request.getHeader("auth");
        if (StringUtils.isBlank(token)) {
            return writer(response, LOGINRESP);
        }
        JwtAccount account = JwtUtil.verifyToken(token, PropertiesUtil.getTokenSecret());
        UserInfoUtil.setUserId(account.getId());
        UserInfoUtil.setUserHeader(account.getHeaderUrl());
        UserInfoUtil.setUserName(account.getAccountName());*/

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

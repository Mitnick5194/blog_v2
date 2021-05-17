package com.ajie.blog.interception;

import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.exception.BlogException;
import com.ajie.chilli.utils.common.JsonUtils;
import com.ajie.commons.RestResponse;
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
    //测试头部，用于api调试，无需认证和判断权限
    private static final String testHeader = "test";

    private static final List<String> BELONG_PATH = new ArrayList<>();

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    static {
        //接口uri规范（如：/blog/delete?id=xxx）
        BELONG_PATH.add("**/delete*/**");
        BELONG_PATH.add("*/update*/**");
    }


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String testAuth = request.getHeader("testAuth");
        //日志追踪id
        MDC.put("reqId", UUID.randomUUID().toString());
        if (testHeader.equals(testAuth)) {
            return true;
        }
        String requestURI = request.getRequestURI();
        //TODO 判断登录
        if (!checkBelong(requestURI, request)) {
            RestResponse<String> rest = RestResponse.fail(BlogExceptionEmun.BELONG_NOT_MATCH.getCode(), BlogExceptionEmun.BELONG_NOT_MATCH.getMsg());
            return writer(response, JsonUtils.toJSONString(rest));
        }

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

    /**
     * 属主判断
     */
    private boolean checkBelong(String requestURI, HttpServletRequest request) {
        if (StringUtils.isBlank(requestURI)) {
            return true;
        }
        if (requestURI.startsWith("/")) {
            //去除/，否则匹配有问题
            requestURI = requestURI.substring(1);
        }
        boolean flag = false;
        for (String s : BELONG_PATH) {
            if (PATH_MATCHER.match(s, requestURI)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return true;
        }
        //判断属主
        String id = request.getParameter("id");
        if (StringUtils.isBlank(id)) {
            return true;
        }
        //获取登录这id TODO
        String userId = "";
        if (id.equals(userId)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String requestURI = "a/v2/blog/delete";
        for (String s : BELONG_PATH) {
            if (PATH_MATCHER.match(s, requestURI)) {
                System.out.println("aa");
            }
        }
    }
}

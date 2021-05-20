package com.ajie.commons.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

public class CookieUtil {

    public static final String LOGIN_TOKEN_KEY = "auth";

    private CookieUtil() {

    }

    /**
     * @param name
     * @param value
     * @param expiry 过期时间，单位s，0表示立即过期，可以用作删除
     */
    public static void setCookie(String name, String value,
                                 int expiry) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            return;
        }
        HttpServletResponse response = requestAttributes.getResponse();
        HttpServletRequest request = requestAttributes.getRequest();
        if (null == value)
            value = "";
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(expiry);
        // 设置域名cookie
        String domain = getDomain(request);
        cookie.setDomain(domain);
        cookie.setPath("/"); // 使用根
        response.addCookie(cookie);
    }


    /**
     * 清除cookie
     *
     * @param name
     */
    public static void clearCookie(String name) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == requestAttributes) {
            return;
        }
        HttpServletResponse response = requestAttributes.getResponse();
        HttpServletRequest request = requestAttributes.getRequest();
        //将值置空
        Cookie cookie = new Cookie(name, "");
        //立即删除
        cookie.setMaxAge(0);
        // 设置域名cookie
        String domain = getDomain(request);
        cookie.setDomain(domain);
        cookie.setPath("/"); // 使用根
        response.addCookie(cookie);
    }

    /**
     * 获取cookie
     *
     * @param request
     * @param name
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (null == cookies || cookies.length == 0) {
            return null;
        }
        for (Cookie c : cookies) {
            String n = c.getName();
            if (name.equals(n)) {
                return c;
            }
        }
        return null;
    }

    /**
     * 获取请求的顶级（如果是二三级域名，需要作处理）域名
     *
     * @param request
     * @return
     */
    private static String getDomain(HttpServletRequest request) {
        String domainName = null;
        String serverName = request.getRequestURL().toString();
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            // 去除http://或者https://
            if (serverName.startsWith("http")) {
                serverName = serverName.substring(7);
            } else {
                serverName = serverName.substring(8);
            }
            // xxx.xxx.xxx/uri
            final int end = serverName.indexOf("/");
            // 去除uri部分
            serverName = serverName.substring(0, end);
            // 去端口
            int portIdx = -1;
            if ((portIdx = serverName.indexOf(":")) > 0) {
                serverName = serverName.substring(0, portIdx);
            }
            final String[] domains = serverName.split("\\.");
            //ip不用处理
            if (isIp(domains)) {
                return serverName;
            }
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn 设置后三层为域名
                domainName = domains[len - 3] + "." + domains[len - 2] + "."
                        + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        return domainName;
    }

    /**
     * 判断是不是ip
     *
     * @param domains
     * @return
     */
    private static boolean isIp(String[] domains) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        for (String str : domains) {
            if (!pattern.matcher(str).matches()) {
                return false;
            }
        }
        return true;
    }
}

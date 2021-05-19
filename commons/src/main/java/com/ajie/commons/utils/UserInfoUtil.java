package com.ajie.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理工具
 */
public class UserInfoUtil {
    /**
     * 用户ID key
     */
    private static final String USER_ID_KEY = "user_id";
    /**
     * 用户名key
     */
    private static final String USER_NAME_KEY = "user_name";
    /**
     * 用户头像链接key
     */
    private static final String USER_HEADER_KEY = "user_header";

    private static ThreadLocal<Map<String, String>> threadLocal;

    private static ThreadLocal<Map<String, String>> getThreadLocal() {
        if (null == threadLocal) {
            synchronized (UserInfoUtil.class) {
                if (null == threadLocal) {
                    threadLocal = new ThreadLocal<>();
                    threadLocal.set(new HashMap<>(4));
                }
            }
        }else{
            Map<String, String> map = threadLocal.get();
            if(null == map){
                threadLocal.set(new HashMap<>(4));
            }
        }
        return threadLocal;
    }

    public static Long getUserId() {
        String s = getValue(USER_ID_KEY);
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return Long.valueOf(s);
    }

    public static String getUserName() {
        return getValue(USER_NAME_KEY);
    }

    public static String getUserHeader() {
        return getValue(USER_HEADER_KEY);
    }

    private static String getValue(String key) {
        Map<String, String> map = getThreadLocal().get();
        return map.get(key);
    }

    public static void setUserId(Long id) {
        Map<String, String> map = getThreadLocal().get();
        map.put(USER_ID_KEY, String.valueOf(id));
    }

    public static void setUserName(String userName) {
        Map<String, String> map = getThreadLocal().get();
        map.put(USER_NAME_KEY, userName);
    }

    public static void setUserHeader(String userHeader) {
        Map<String, String> map = getThreadLocal().get();
        map.put(USER_HEADER_KEY, userHeader);
    }
}

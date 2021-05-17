package com.ajie.commons.utils;

import com.ajie.commons.po.BasePO;

/**
 * 用户管理工具
 */
public class UserInfoUtil {

    private static ThreadLocal<BasePO> threadLocal;

    private static ThreadLocal<BasePO> getThreadLocal() {
        if (null == threadLocal) {
            synchronized (UserInfoUtil.class) {
                if (null == threadLocal) {
                    threadLocal = new ThreadLocal<>();
                }
            }
        }
        return threadLocal;
    }

    public static <T extends BasePO> void set(T user) {
        getThreadLocal().set(user);
    }

    public static <T extends BasePO> T get() {
        BasePO po = (T) getThreadLocal().get();
        if (null == po) {
            po = new BasePO();
        }
        return (T) po;
    }

}

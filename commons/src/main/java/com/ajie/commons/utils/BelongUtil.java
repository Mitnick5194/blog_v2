package com.ajie.commons.utils;

import java.util.Map;

/**
 * 属主工具，更新和删除操作需要判断数据属主
 */
public class BelongUtil {
    private static ThreadLocal<Boolean> threadLocal;

    private static ThreadLocal<Boolean> getThreadLocal() {
        if (null == threadLocal) {
            synchronized (UserInfoUtil.class) {
                if (null == threadLocal) {
                    threadLocal = new ThreadLocal<>();
                }
            }
        }
        return threadLocal;
    }

    /**
     * 完成判断，后面可通行
     */
    private static void checkComplete() {
        getThreadLocal().set(true);
    }

    /**
     * 是否已完成属主判断
     *
     * @return
     */
    private static boolean isCheck() {
        return getThreadLocal().get();
    }

    /**
     * 清楚
     */
    private static void clear() {
        getThreadLocal().set(false);
    }
}

package com.ajie.blog.account.config;

/**
 * 静态方式获取配置
 */
public class PropertiesUtil {
    private static Properties properties;

    public static void init(Properties p) {
        if (null == properties) {
            synchronized (PropertiesUtil.class) {
                if (null == properties) {
                    properties = p;
                }
            }
        }
    }

    public static String getTokenSecret() {
        return properties.getTokenSecret();
    }
}

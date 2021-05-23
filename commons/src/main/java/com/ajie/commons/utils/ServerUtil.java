package com.ajie.commons.utils;

public class ServerUtil {

    public static final String DEV_FLAG = "dev";

    private ServerUtil() {

    }

    public static boolean isDev() {
        String env = System.getProperty("env");
        return DEV_FLAG.equals(env);
    }
}

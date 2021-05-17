package com.ajie.commons.utils;

public class ParamCheck {
    public static void assertNull(Object obj, RuntimeException e) {
        if (null == obj) {
            throw e;
        }
    }

    public static void assertNull(RuntimeException e, Object... objs) {
        if (null == objs || objs.length == 0) {
            return;
        }
        for (Object obj : objs) {
            if (null == obj) {
                throw e;
            }
        }

    }
}

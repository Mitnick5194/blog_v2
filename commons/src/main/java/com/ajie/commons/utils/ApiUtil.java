package com.ajie.commons.utils;

import com.ajie.commons.RestResponse;
import com.ajie.commons.exception.MicroCommonException;

public class ApiUtil {

    private ApiUtil() {

    }

    public static <T> void checkSuccess(RestResponse<T> data) {
        int code = data.getCode();
        if (code == 200) {
            return;
        }
        throw MicroCommonException.REMOTE_CALL_FAIL;
    }

    public static <T> T checkAndGetData(RestResponse<T> data) {
        checkSuccess(data);
        Object obj = data.getData();
        if (null == obj) {
            return null;
        }
        return (T) obj;
    }
}

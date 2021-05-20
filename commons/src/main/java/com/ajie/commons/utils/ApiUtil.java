package com.ajie.commons.utils;

import com.ajie.commons.RestResponse;
import com.ajie.commons.exception.MicroCommonException;
import com.alibaba.fastjson.JSONObject;

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
        JSONObject obj = (JSONObject) data.getData();
        Object o = obj.toJavaObject(Object.class);
        return (T) o;
    }
}

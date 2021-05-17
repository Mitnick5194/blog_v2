package com.ajie.commons;

/**
 * rest响应
 *
 * @param <T>
 */
public class RestResponse<T> {

    /**
     * 成功状态码
     */
    public static int CODE_SUC = 200;

    /**
     * 登录过期
     */
    public static final int NO_LOGIN = 401;

    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;

    /**
     * 服务异常
     */
    public static int CODE_ERR = 500;

    /**
     * 状态码
     */
    protected int code;

    /**
     * 返回消息
     */
    protected String msg;

    /**
     * 返回数据
     */
    protected T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @param
     * @return
     */
    public static <T> RestResponse success(T data) {
        RestResponse ret = new RestResponse();
        ret.setCode(CODE_SUC);
        ret.setMsg("请求成功");
        ret.setData(data);
        return ret;
    }

    /**
     * 成功
     *
     * @param
     * @return
     */
    public static <T> RestResponse success() {
        RestResponse ret = new RestResponse();
        ret.setCode(CODE_SUC);
        ret.setMsg("请求成功");
        ret.setData((T) null);
        return ret;
    }


    public static <T> RestResponse fail(String msg) {
        RestResponse ret = new RestResponse();
        ret.setCode(CODE_ERR);
        ret.setMsg(msg);
        ret.setData((T) null);
        return ret;
    }

    public static <T> RestResponse fail(int code,String msg) {
        RestResponse ret = new RestResponse();
        ret.setCode(code);
        ret.setMsg(msg);
        ret.setData((T) null);
        return ret;
    }
}

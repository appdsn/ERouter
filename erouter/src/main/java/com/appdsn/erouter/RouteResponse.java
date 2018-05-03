package com.appdsn.erouter;

/**
 * Created by wbz360 on 2018/3/14.
 */
public class RouteResponse {
    public static final int CODE_SUCCESS = 0x0000;
    public static final int CODE_ERROR = 0x0001;
    public static final int CODE_NOT_FOUND = 0X0002;
    public static final int CODE_INTERCEPT = 0X0003;
    public static final int CODE_TIMEOUT = 0X0004;
    public static final int CODE_RESPONSE_NULL = 0X0005;
    public int code;
    public String msg;
    public Object data;
    public Object component;//组件对应的实例

    public RouteResponse() {
    }

    public RouteResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RouteResponse code(int code) {
        this.code = code;
        return this;
    }

    public RouteResponse msg(String msg) {
        this.msg = msg;
        return this;
    }

    public RouteResponse data(Object data) {
        this.data = data;
        return this;
    }

    public RouteResponse component(Object component) {
        this.component = component;
        return this;
    }
}

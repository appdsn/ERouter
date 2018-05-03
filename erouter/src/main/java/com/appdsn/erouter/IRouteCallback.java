package com.appdsn.erouter;

/**
 * Created by wbz360 on 2018/3/14.
 */
public interface IRouteCallback {
    void onFailed(String error);
    void onSucess(RouteResponse response);
}

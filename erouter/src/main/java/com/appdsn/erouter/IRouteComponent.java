package com.appdsn.erouter;

import android.content.Context;

/**
 * Created by wbz360 on 2018/3/14.
 */
public interface IRouteComponent{
    RouteResponse onCall(Context context, RouteRequest request);
}

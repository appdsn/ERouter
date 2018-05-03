package com.appdsn.erouterdemo;

import android.content.Context;

import com.appdsn.erouter.IRouteComponent;
import com.appdsn.erouter.RouteRequest;
import com.appdsn.erouter.RouteResponse;


/**
 * Created by wbz360 on 2018/3/12.
 */

public class CustomComponent implements IRouteComponent {
    @Override
    public RouteResponse onCall(Context context, RouteRequest request) {
        return new RouteResponse(RouteResponse.CODE_SUCCESS,"提示信息","我是返回的数据");
    }
}

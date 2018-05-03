package com.appdsn.erouter;

/**
 * Created by wbz360 on 2018/3/14.
 */
public interface IRouteInterceptor {

    /**
     * 拦截器可以修改RouteRequest,并可以通过return chain.proceed()将请求传递下去
     * 也可以不调用proceed，拦截请求后直接返回RouteResponse作为最终的结果
     * 注意：拦截器是在用户设置的线程中执行的，默认当前线程，注意在拦截器中操作UI
     */
    RouteResponse intercept(RouteChain chain);
}

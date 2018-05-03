package com.appdsn.erouter;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

/**
 * Created by wbz360 on 2018/3/14.
 */
public class RouteChain {

    private List<IRouteInterceptor> interceptors;
    private RouteRequest request;
    private int index;
    private RouteHandler routeHandler;
    private Object context; //可能是Context，也可能是Fragment

    public RouteChain(Object context, RouteRequest request) {
        this.request = request;
        this.context = context;
        routeHandler = new RouteHandler();
        interceptors = ERouter.interceptors;
        index = -1;
    }

    /*同步返回组件的实例或者intent实例，异步就返回null，可以在RouteResponse中获取*/
    public <T> T call() {
        if (context == null) {
            handleResult(new RouteResponse(RouteResponse.CODE_ERROR
                    , "Context can not is null", ""), request.getCallBack());
            return null;
        }
        //拦截器返回的结果，如果都没有拦截，最后一般会返回null
        if (request.isAsync()) {
            handleAsyncTask();
            return null;
        } else {
            RouteResponse response = proceed(request);
            if (index < interceptors.size() && response == null) {//被拦截了且没有正常返回结果
                IRouteInterceptor interceptor = interceptors.get(index);
                response = new RouteResponse(RouteResponse.CODE_INTERCEPT, "intercepted by " + interceptor.getClass().getName(), "");
            }
            handleResult(response, request.getCallBack());
            return (T) response.component;
        }

    }

    private void handleAsyncTask() {
        AsyncTask task = new AsyncTask<Object, Void, RouteResponse>() {
            private Handler timeOutHandler;
            private Runnable timeOutRunable;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                timeOutHandler = new Handler(Looper.getMainLooper());
                timeOutRunable = new Runnable() {
                    @Override
                    public void run() {
                        cancel(true);
                    }
                };
                timeOutHandler.postDelayed(timeOutRunable, request.getTimeout());
            }

            @Override
            protected RouteResponse doInBackground(Object... params) {
                RouteResponse response = proceed(request);
                return response;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                handleResult(new RouteResponse(RouteResponse.CODE_TIMEOUT, "TIME OUT", "")
                        , request.getCallBack());
            }

            @Override
            protected void onPostExecute(RouteResponse response) {
                timeOutHandler.removeCallbacks(timeOutRunable);
                if (index < interceptors.size() && response == null) {//被拦截了且没有正常返回结果
                    IRouteInterceptor interceptor = interceptors.get(index);
                    response = new RouteResponse(RouteResponse.CODE_INTERCEPT, "intercepted by " + interceptor.getClass().getName(), "");
                }
                handleResult(response, request.getCallBack());
            }
        };
        task.execute();
    }

    public RouteResponse proceed(RouteRequest request) {
        this.request = request;
        index++;//默认值为-1，所以从0开始
        if (index >= interceptors.size()) {//所有拦截器都没有拦截，或没有拦截器，则执行route请求
            return routeHandler.route(context, request);
        }
        IRouteInterceptor interceptor = interceptors.get(index);
        //处理异常情况：如果为拦截器为null，则执行下一个
        if (interceptor == null) {
            return proceed(request);
        }

        RouteResponse response = interceptor.intercept(this);
        return response;//返回结果给上一个拦截器，拦截器还可以拦截结果
    }

    public void handleResult(RouteResponse response, IRouteCallback callback) {
        if (response == null) {
            if (callback != null) {
                callback.onFailed("response is null");
            }
        } else if (response.code == RouteResponse.CODE_SUCCESS) {
            if (callback != null) {
                callback.onSucess(response);
            }
        } else {
            if (callback != null) {
                callback.onFailed(response.msg);
            }
        }
    }

    //返回拦截器处理后的最新请求
    public RouteRequest getRequest() {
        return request;
    }

    public Object getContext() {
        return context;
    }
}

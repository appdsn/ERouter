package com.appdsn.erouter;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.net.URLDecoder;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by wbz360 on 2018/3/14.
 */
public class RouteRequest {
    private Bundle mBundle;         // Data to transform
    private boolean async = false; //是否异步执行
    private Uri uri;
    private int flags = -1;
    private int timeout = 3000;//异步任务超时时间默认3秒
    private String componentName;
    private Class<?> componentClzz;
    private int requestCode = -1;
    private int inAnimation=-1;
    private int outAnimation=-1;
    private IRouteCallback callBack;
    private boolean login = false;

    public RouteRequest(String componentName) {
        setComponentName(componentName);
        this.mBundle = new Bundle();
    }

    public RouteRequest(Uri uri) {
        this.mBundle = new Bundle();
        this.uri = uri;
        parseUri(uri);
    }

    //类似于Http://www.baidu.com/main/MainActivity?usename=xxxx&pwd=xxxx
    //path用于获取组件的类名，比如main/MainActivity，需要的是MainActivity
    private void parseUri(Uri uri) {
        String path = uri.getPath();//如：/main/MainActivity
        if (!TextUtils.isEmpty(path)) {
            String targetSimpleName = path.substring(path.lastIndexOf("/") + 1);
            setComponentName(targetSimpleName);//如MainActivity
        }
        String query = uri.getEncodedQuery();//usename=xxxx&pwd=xxxx
        if (!TextUtils.isEmpty(query)) {
            Map<String, String> params = new IdentityHashMap<>();
            String[] split = query.split("&");
            for (String param : split) {
                if (!param.contains("=")) {
                    continue;
                }
                int index = param.indexOf("=");
                String key = new String(param.substring(0, index));
                String value = URLDecoder.decode(param.substring(index + 1));
                mBundle.putString(key, value);
            }
        }
    }

    public Uri getUri() {
        return uri;
    }

    public RouteRequest setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public IRouteCallback getCallBack() {
        return callBack;
    }

    public RouteRequest setCallBack(IRouteCallback callBack) {
        this.callBack = callBack;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public RouteRequest setAsync(boolean async) {
        this.async = async;
        return this;
    }

    /*设置跳转页面是否需要登录*/
    public boolean isNeedLogin() {
        return login;
    }

    public RouteRequest setNeedLogin(boolean login) {
        this.login = login;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public RouteRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Class<?> getComponentClzz() {
        return componentClzz;
    }

    public RouteRequest setComponentClzz(Class<?> componentClzz) {
        this.componentClzz = componentClzz;
        return this;
    }

    public int getInAnim() {
        return inAnimation;
    }

    public RouteRequest setInAnim(int inAnimation) {
        this.inAnimation = inAnimation;
        return this;
    }

    public int getOutAnim() {
        return outAnimation;
    }

    public RouteRequest setOutAnim(int outAnimation) {
        this.outAnimation = outAnimation;
        return this;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public RouteRequest setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public RouteRequest setComponentName(String clsName) {
        this.componentName = clsName;
        return this;
    }

    public String getComponentName() {
        return componentName;
    }

    public RouteRequest setFlags(int flag) {
        this.flags |= flags;
        return this;
    }

    public int getFlags() {
        return flags;
    }

    public Bundle getExtras() {
        return mBundle;
    }

    public RouteRequest addExtras(Bundle bundle) {
        if (bundle != null) {
            mBundle.putAll(bundle);
        }
        return this;
    }

    public <T> T route(Context context) {
        return ERouter.getInstance().route(context, this);
    }

    public <T> T route(Context context, IRouteCallback callback) {
        setCallBack(callback);
        return ERouter.getInstance().route(context, this);
    }

    public <T> T route(Fragment context) {
        return ERouter.getInstance().route(context, this);
    }

    public <T> T route(android.support.v4.app.Fragment context) {
        return ERouter.getInstance().route(context, this);
    }

    public <T> T route(Fragment context, IRouteCallback callback) {
        return ERouter.getInstance().route(context, this);
    }

    public <T> T route(android.support.v4.app.Fragment context, IRouteCallback callback) {
        return ERouter.getInstance().route(context, this);
    }
}

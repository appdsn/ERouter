package com.appdsn.erouter;

import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wbz360 on 2018/3/11 0011.
 * 主要功能：
 * 1：自动注册android四大组件 + 手动注册自定义组件和Fragment
 * 2：页面跳转传值：通过组件字符串类名，或标准URI自动跳转（activity，service）
 * 3：跨模块调用：可以获取如Fragment，Intent，自定义组件实例等。
 * 3：自定义拦截器：实现统一逻辑，如登录判断，修改请求参数，降级处理等
 * 4：支持同步异步调用自定义组件
 * 5：支持结果的回调
 * 6：支持异步任务超时设置
 * 7：支持Fragment的startActivityForResult
 * 未来计划：
 * 1，模块间跨进程通信
 */
public final class ERouter {
    private static ERouter instance = null;
    public static HashMap<String, Class<?>> components;
    private static Context mContext;
    private static boolean hasInit = false;
    public static List<IRouteInterceptor> interceptors = new ArrayList<>();

    private ERouter() {

    }

    public static void init(Application application) {
        if (!hasInit) {
            mContext = application;
            components = registerAll(application);
            hasInit = true;
        }
    }

    public static ERouter getInstance() {
        if (!hasInit) {
            throw new RuntimeException("Router::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (ERouter.class) {
                    if (instance == null) {
                        instance = new ERouter();
                    }
                }
            }
            return instance;
        }
    }


    public static void registerComponent(Class<?> componentClzz) {
        components.put(componentClzz.getSimpleName(), componentClzz);
    }

    /**
     * 添加组件调用前的拦截器
     */
    public static void addInterceptor(IRouteInterceptor interceptor) {
        if (interceptor != null) {
            interceptors.add(interceptor);
        }
    }

    /*componentName是一个组件类的SimpleName*/
    public RouteRequest build(String componentName) {
        RouteRequest request = new RouteRequest(componentName);
        return request;
    }

    public RouteRequest build(Class<?> componentClzz) {
        if (!components.containsValue(componentClzz)) {
            registerComponent(componentClzz);
        }
        RouteRequest request = new RouteRequest(componentClzz.getSimpleName());
        return request;
    }

    public RouteRequest build(Uri uri) {
        RouteRequest request = new RouteRequest(uri);
        return request;
    }

    public <T> T route(Context context, RouteRequest request) {
        return new RouteChain(context, request).call();
    }

    /*支持Fragment的startActivityForResult*/
    public <T> T route(Fragment fragment, RouteRequest request) {
        return new RouteChain(fragment, request).call();
    }

    /*支持Fragment的startActivityForResult*/
    public <T> T route(android.support.v4.app.Fragment fragment, RouteRequest request) {
        return new RouteChain(fragment, request).call();
    }

    // 获取 AndroidManifest.xml 注册的所有Activity Service
    private static HashMap<String, Class<?>> registerAll(Context context) {
        HashMap<String, Class<?>> components = new HashMap<>();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (packInfo != null && packInfo.activities != null) {
            for (ActivityInfo info : packInfo.activities) {
                try {
                    components.put(info.name, Class.forName(info.name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (packInfo != null && packInfo.services != null) {
            for (ServiceInfo info : packInfo.services) {
                try {
                    components.put(info.name, Class.forName(info.name));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return components;
    }
}

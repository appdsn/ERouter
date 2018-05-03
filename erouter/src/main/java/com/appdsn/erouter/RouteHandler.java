package com.appdsn.erouter;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wbz360 on 2018/3/14.
 */
public class RouteHandler {

    public RouteResponse route(Object _context, RouteRequest request) {
        Context context = null;
        if (_context instanceof Context) {
            context = (Context) _context;
        } else if (_context instanceof Fragment) {
            context = ((Fragment) _context).getActivity();
        } else if (_context instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) _context).getActivity();
        }
        HashMap<String, Class<?>> components = ERouter.components;
        Class<?> componentClazz = null;
        for (Map.Entry<String, Class<?>> component : components.entrySet()) {
            String componentName = getSimpleName(component.getKey());
            Log.i("123", "componentName:" + componentName);
            if (componentName.toLowerCase().startsWith(request.getComponentName().toLowerCase())) {
                componentClazz = component.getValue();
                break;
            }
        }
        if (componentClazz == null) {
            return new RouteResponse(RouteResponse.CODE_NOT_FOUND, "没有找到对应的组件类:" + request.getComponentName(), "");
        } else {
            request.setComponentClzz(componentClazz);
        }

        RouteResponse response = null;
        if (Activity.class.isAssignableFrom(componentClazz)) {
            response = startActivity(context, _context, request);
        } else if (Service.class.isAssignableFrom(componentClazz)) {
            response = startService(context, request);
        } else if (Fragment.class.isAssignableFrom(componentClazz)) {
            response = createFragment(request);
        } else if (android.support.v4.app.Fragment.class.isAssignableFrom(componentClazz)) {
            response = createFragment(request);
        } else if (IRouteComponent.class.isAssignableFrom(componentClazz)) {
            response = doCall(context, request);
        } else {
            try {
                Object instance = request.getComponentClzz().getConstructor().newInstance();
                response = new RouteResponse(RouteResponse.CODE_SUCCESS, "Object create sucess", "");
                response.component(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (response == null) {
            response = new RouteResponse(RouteResponse.CODE_ERROR, "response is null", "");
        }
        return response;
    }


    private RouteResponse createFragment(RouteRequest request) {
        Class fragmentClass = request.getComponentClzz();
        RouteResponse response = null;
        try {
            Object instance = fragmentClass.getConstructor().newInstance();
            if (instance instanceof Fragment) {
                ((Fragment) instance).setArguments(request.getExtras());
            } else if (instance instanceof android.support.v4.app.Fragment) {
                ((android.support.v4.app.Fragment) instance).setArguments(request.getExtras());
            }
            response = new RouteResponse(RouteResponse.CODE_SUCCESS, "fragment create sucess", "");
            response.component(instance);

        } catch (Exception e) {
            e.printStackTrace();
            response = new RouteResponse(RouteResponse.CODE_ERROR, e.getMessage(), "");
        }
        return response;
    }

    /*只有IRouteComponent类型的组件才会有异步和同步调用*/
    private RouteResponse doCall(Context context, RouteRequest request) {
        Class<?> componentClzz = request.getComponentClzz();
        RouteResponse response = null;
        try {
            IRouteComponent component = (IRouteComponent) componentClzz.getConstructor().newInstance();
            if (component != null) {
                response = component.onCall(context, request);
                if (response == null) {
                    response = new RouteResponse(RouteResponse.CODE_RESPONSE_NULL, "response is null", "");
                }
                response.component(component);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new RouteResponse(RouteResponse.CODE_ERROR, e.getMessage(), "");
        }
        return response;
    }

    public RouteResponse startService(Context context, RouteRequest request) {
        Intent intent = new Intent(context, request.getComponentClzz());
        context.startActivity(intent);
        RouteResponse response = new RouteResponse(RouteResponse.CODE_SUCCESS, "service start sucess", "");
        response.component(intent);
        return response;
    }

    public RouteResponse startActivity(Context context, Object _context, RouteRequest request) {
        Log.i("123", "startActivity:");

        Activity activity = null;
        Fragment fragment = null;
        android.support.v4.app.Fragment fragment1 = null;

        if (_context instanceof Activity) {
            activity = (Activity) _context;
        } else if (_context instanceof Fragment) {
            fragment = (Fragment) _context;
        } else if (_context instanceof android.support.v4.app.Fragment) {
            fragment1 = (android.support.v4.app.Fragment) _context;
        }

        Intent intent = new Intent(context, request.getComponentClzz());
        if (request.getExtras() != null) {
            intent.putExtras(request.getExtras());
        }
        if (request.getFlags() >= 0) {
            intent.addFlags(request.getFlags());
        }

        if (activity != null) {
            activity.startActivityForResult(intent, request.getRequestCode());
            int inAnimation = request.getInAnim();
            int outAnimation = request.getOutAnim();
            if (inAnimation >= 0 || outAnimation >= 0) {
                activity.overridePendingTransition(inAnimation, outAnimation);
            }
        } else if (fragment != null) {
            fragment.startActivityForResult(intent, request.getRequestCode());
            int inAnimation = request.getInAnim();
            int outAnimation = request.getOutAnim();
            if (inAnimation >= 0 || outAnimation >= 0) {
                fragment.getActivity().overridePendingTransition(inAnimation, outAnimation);
            }
        } else if (fragment1 != null) {
            fragment1.startActivityForResult(intent, request.getRequestCode());
            int inAnimation = request.getInAnim();
            int outAnimation = request.getOutAnim();
            if (inAnimation >= 0 || outAnimation >= 0) {
                fragment1.getActivity().overridePendingTransition(inAnimation, outAnimation);
            }
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        RouteResponse response = new RouteResponse(RouteResponse.CODE_SUCCESS, "activity start sucess", "");
        response.component(intent);
        return response;
    }

    /**
     * 获取 SimpleName
     */
    private static String getSimpleName(String clsName) {
        String simpleName = clsName;
        if (!TextUtils.isEmpty(clsName) && clsName.contains(".")) {
            int lastIndex = clsName.lastIndexOf(".");
            simpleName = clsName.substring(lastIndex + 1, clsName.length());
        }
        return simpleName;
    }
}

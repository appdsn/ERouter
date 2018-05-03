框架简介<br>
=
 模块化开发中最大的问题就是组件间通讯，ERoute使用简单，支持四大组件的静态自动注册，以及自定义组件的动态注册。
 * 1：自动注册android四大组件 + 手动注册自定义组件和Fragment
 * 2：页面跳转传值：通过组件字符串类名，或标准URI自动跳转（activity，service）
 * 3：跨模块调用：可以获取如Fragment，Intent，自定义组件实例等。
 * 3：自定义拦截器：实现统一逻辑，如登录判断，修改请求参数，降级处理等
 * 4：支持同步异步调用自定义组件
 * 5：支持结果的回调
 * 6：支持异步任务超时设置
 * 7：支持Fragment的startActivityForResult

使用方法
=

（1）初始化路由
--
```java 
   ERouter.init(getApplication());
 ```
 
（2）通过组件名路由
-
>比如现在有一个登录界面LoginActivity，我们想启动这个Activity，可以使用如下方式：
```java   
ERouter.getInstance().build("loginactivity").route(MainActivity.this);
```

（3）通过URL路由
-
>这个Url一般是从第三方跳转过来携带的数据，可以是自定义的协议

```java
Uri uri=Uri.parse("Http://www.xxxx.com/main/loginactivity?usename=aa&pwd=qq");
ERouter.getInstance().build(uri).route(MainActivity.this);
```

（4）自定义组件
-

```java
public class CustomComponent implements IRouteComponent {
    @Override
    public RouteResponse onCall(Context context, RouteRequest request) {
        return new RouteResponse(RouteResponse.CODE_SUCCESS,"提示信息","返回的数据");
    }
}

ERouter.registerComponent(CustomComponent.class);//别忘记注册

 ```
（5）获取自定义组件返回的数据
-
```java
    ERouter.getInstance().build("CustomComponent").route(MainActivity.this, new IRouteCallback() {
            @Override
            public void onFailed(String error) {

            }

            @Override
            public void onSucess(RouteResponse response) {
                response.data//取得组件返回的数据
            }
    });
```
（6）自定义拦截器
-
>自定义拦截器，实现IRouteInterceptor接口，可以在拦截器中实现统一逻辑，如：登录判断，修改请求参数，出错降级处理等。
```java
 ERouter.addInterceptor(new IRouteInterceptor() {
            @Override
            public RouteResponse intercept(RouteChain chain) {
                //什么也不处理要往下传递
                return chain.proceed(chain.getRequest());
            }
});
```

（7）请求参数设置
-
>RouteRequest有很多参数设置，支持链式调用
```java
addExtras()//额外的数据
setAsync()//是否异步执行，默认false
setInAnim()//设置activity进入动画
setOutAnim()//设置activity退出动画
setCallBack()//设置路由成功回调
setFlags()//设置activity的Flags
setNeedLogin()//设置是否需要登录
setRequestCode()//activity请求码
setTimeout()//异步执行超时时间
setUri()//uri路由
setComponentClzz()//组件名路由
```
联系方式
-
* Email：2792889279@qq.com
* qq： 2792889279

Licenses
-
        
        Copyright 2018 wbz360(王宝忠)

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

         　　　　http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.







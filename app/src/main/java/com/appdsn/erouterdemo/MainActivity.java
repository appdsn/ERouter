package com.appdsn.erouterdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appdsn.erouter.ERouter;
import com.appdsn.erouter.IRouteCallback;
import com.appdsn.erouter.IRouteInterceptor;
import com.appdsn.erouter.RouteChain;
import com.appdsn.erouter.RouteResponse;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ERouter.init(getApplication());//初始化
    }

    public void doClick(View view) {

        switch (view.getId()) {
            case R.id.btnRoute3:
                ERouter.addInterceptor(new IRouteInterceptor() {
                    @Override
                    public RouteResponse intercept(RouteChain chain) {
                        Toast.makeText(MainActivity.this, "拦截器被调用", Toast.LENGTH_SHORT).show();
                        return chain.proceed(chain.getRequest());
                    }
                });
                Toast.makeText(MainActivity.this, "添加拦截器成功", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btnRoute1:
                ERouter.getInstance().build("targetactivity").route(MainActivity.this);
                break;
            case R.id.btnRoute2:
                Uri uri = Uri.parse("Http://www.baidu.com/main/targetactivity?usename=aa&pwd=qq");
                ERouter.getInstance().build(uri).route(MainActivity.this);
                break;

            case R.id.btnRoute4:
                ERouter.registerComponent(CustomComponent.class);
                Uri uri3 = Uri.parse("Http://www.baidu.com/main/CustomComponent?usename=aa&pwd=qq");
                ERouter.getInstance().build(uri3).
                        route(MainActivity.this, new IRouteCallback() {
                            @Override
                            public void onFailed(String error) {
                                Log.i("123", "onFailed:" + error);
                            }

                            @Override
                            public void onSucess(RouteResponse response) {
                                Toast.makeText(MainActivity.this, "自定义组件：" + response.data, Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            default:
                break;
        }


    }
}

package com.harine.virgotest;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.harine.virgotest.service.CoreService;
import com.qweather.sdk.view.HeConfig;

/**
 * @author nepalese on 2021/4/2 14:55
 * @usage
 */
public class MyApplication extends Application {
    private static MyApplication application;

    public MyApplication(){
        application = this;
    }

    public static MyApplication getApplication(){
        if(application==null){
            synchronized (MyApplication.class){
                if(application==null){
                    application = new MyApplication();
                    application.onCreate();
                }
            }
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
//        startService(new Intent(this, CoreService.class));
        //weatheer进行账户初始化
//        HeConfig.init("HE2104231014221889", "b82be37b435d42a1ab509705eac31984");
//        //切换至开发版服务
//        HeConfig.switchToDevService();
        //切换至商业版服务:默认
//        HeConfig.switchToBizService();
    }
}

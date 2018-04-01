package com.example.bill.myapplication;

import android.app.Application;

/**
 * Created by Bill on 2018/4/1.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 开启异常处理
        CrashHandler.getInstance().init(this);
    }
}

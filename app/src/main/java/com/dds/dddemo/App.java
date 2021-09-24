package com.dds.dddemo;

import android.app.Application;

/**
 * Created by dds on 2019/6/14.
 * android_shuai@163.com
 */
public class App extends Application {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App getApp() {
        return app;
    }
}

package com.dds.dddemo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.dds.common.lifecycle.Utils;

/**
 * Created by dds on 2019/6/14.
 * android_shuai@163.com
 */
public class App extends Application implements Utils.OnAppStatusChangedListener {
    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Utils.init(app);
        Utils.registerAppStatusChangedListener(this);
    }

    public static App getApp() {
        return app;
    }

    @Override
    public void onForeground(Activity activity) {
        Log.d("dds_test","onForeground");
    }

    @Override
    public void onBackground(Activity activity) {
        Log.d("dds_test","onBackground");
    }
}

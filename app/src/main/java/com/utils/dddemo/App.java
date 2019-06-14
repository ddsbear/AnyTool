package com.utils.dddemo;

import android.app.Application;

import com.dds.dblibrary.LitePalUtil;

/**
 * Created by dds on 2019/6/14.
 * android_shuai@163.com
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LitePalUtil.init(this);
    }
}

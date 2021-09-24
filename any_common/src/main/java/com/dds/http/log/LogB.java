package com.dds.http.log;

import android.util.Log;

import com.dds.http.BuildConfig;

/**
 * Created by dds on 2019/10/17.
 * android_shuai@163.com
 */
public class LogB {
    public static boolean debug = true;

    public static void d(String tag, String msg) {
        if (debug || BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug || BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }
    public static void i(String tag, String msg) {
        if (debug || BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }
}

package com.utils.library.log;

import android.util.Log;

/**
 * Created by dds on 2019/10/18.
 * android_shuai@163.com
 */
public class DefDelegate implements LogA.LogDelegate {
    @Override
    public void e(String tag, String msg, Object... obj) {
        Log.e(tag, String.format(msg, obj));

    }

    @Override
    public void w(String tag, String msg, Object... obj) {
        Log.w(tag, String.format(msg, obj));
    }

    @Override
    public void i(String tag, String msg, Object... obj) {
        Log.i(tag, String.format(msg, obj));
    }

    @Override
    public void d(String tag, String msg, Object... obj) {
        Log.d(tag, String.format(msg, obj));
    }

    @Override
    public void printErrStackTrace(String tag, Throwable tr, String format, Object... obj) {
        Log.e(tag, String.format(format, obj));
        Log.e(tag, Log.getStackTraceString(tr));
    }
}

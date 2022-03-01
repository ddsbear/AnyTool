package com.dds.dddemo.hack;

import android.util.Log;

import com.dds.common.log.LogUtils;

public class HackDemo {
    private int mIntField;
    private String mStr;

    private static String staticField = "dds";

    private HackDemo() {
        Log.d("dds_test", "constructor");
    }

    private HackDemo(int x) {
        mIntField = x;
        Log.d("dds_test", "constructor " + x);
    }

    private HackDemo(int x, String str) {
        mIntField = x;
        mStr = str;
        Log.d("dds_test", "constructor " + x);
    }

    private int foo() {
        Log.d("dds_test", "method :foo");
        return mIntField;
    }

    private int foo(int type, String str) {
        Log.d("dds_test", "method :foo " + type + "," + str);
        return 7;
    }

    private static void bar() {
        Log.d("dds_test", "static method :bar");
    }

    private static int bar(int type) {
        Log.d("dds_test", "static method :bar " + type);
        return type;
    }

    private static void bar(int type, String name, Bean bean) {
        LogUtils.d("dds_test", "static method :bar type:%d,%s,%s", type, name, bean.toString());
    }


    public void printStaticField() {
        Log.d("dds_test", "printStaticField:" + staticField);
    }
}

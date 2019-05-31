package com.trustmobi.dddemo.hack;

import java.io.IOException;

/**
 * Created by dds on 2019/5/31.
 * android_shuai@163.com
 */
public class HackDemo {

    private int mIntField;

    private HackDemo(final int x) throws IOException {
        mIntField = x;
    }


    private int foo() {
        return 7;
    }

    private static void bar(final int type, final String name, final HackDemo simple) throws IOException {
    }


}

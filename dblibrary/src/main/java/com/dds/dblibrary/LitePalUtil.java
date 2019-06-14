package com.dds.dblibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.litepal.LitePal;

/**
 * Created by dds on 2019/6/14.
 * android_shuai@163.com
 */
public class LitePalUtil {

    //初始化
    public static void init(Context context) {
        LitePal.initialize(context);
    }

    public static void getDatabase() {
        SQLiteDatabase db = LitePal.getDatabase();
    }
}

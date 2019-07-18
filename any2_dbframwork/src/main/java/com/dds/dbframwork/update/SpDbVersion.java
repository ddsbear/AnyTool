package com.dds.dbframwork.update;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dds on 2019/7/18.
 * android_shuai@163.com
 */
public class SpDbVersion {

    public final static String DB_VERSION = "db_version";
    public final static String HANDLE_DB_VERSION = "db_handle";

    public static void setDbVersionCode(Context context, int version) {
        SharedPreferences preferences = context.getSharedPreferences(DB_VERSION, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HANDLE_DB_VERSION, version);
        editor.apply();
    }

    public static int getDbVersionCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DB_VERSION, MODE_PRIVATE);
        return preferences.getInt(HANDLE_DB_VERSION, 0);
    }
}

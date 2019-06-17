package com.dds.jetpack.room;

import android.app.Application;

import androidx.room.Room;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */
public class DbInstance {
    private static final String DB_NAME = "room_db";
    private static AppDataBase appDataBase;


    public static AppDataBase getInstance(Application context) {
        if (appDataBase == null) {
            synchronized (DbInstance.class) {
                if (appDataBase == null) {
                    return Room.databaseBuilder(context, AppDataBase.class, DB_NAME)
                            .build();
                }
            }
        }
        return appDataBase;
    }
}

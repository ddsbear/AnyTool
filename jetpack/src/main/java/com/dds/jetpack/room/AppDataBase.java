package com.dds.jetpack.room;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dds.jetpack.room.dao.MessageDao;
import com.dds.jetpack.room.dao.UserDao;
import com.dds.jetpack.room.entity.MessageBean;
import com.dds.jetpack.room.entity.UserInfo;
import com.dds.jetpack.room.utils.Converters;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */

@Database(entities = {UserInfo.class, MessageBean.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDataBase extends RoomDatabase {

    public abstract UserDao getUserDao();

    public abstract MessageDao getMessageDao();

    private static final String DB_NAME = "room_db";
    // 静态内部类
    private static class Holder {
        private volatile static AppDataBase appDataBase = null;

        static AppDataBase getInstance(Context context) {
            if (appDataBase == null) {
                synchronized (AppDataBase.class) {
                    if (appDataBase == null) {
                        appDataBase = buildDatabase(context);
                    }
                }
            }
            return appDataBase;
        }

        private static AppDataBase buildDatabase(Context context) {
            return Room.databaseBuilder(context, AppDataBase.class, DB_NAME)
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // 第一次创建数据库会回调此方法，可以做初始化数据之类的操作
                            Log.e(DB_NAME, "room_db 数据库第一次创建成功！");

                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            Log.e(DB_NAME, "room_db 数据库 onOpen！");
                        }
                    })
                    .build();
        }
    }
    // 向外提供方法
    public static AppDataBase getAppDataBase(Context context) {
        return Holder.getInstance(context);
    }
}

package com.dds.jetpack.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dds.jetpack.room.dao.MessageDao;
import com.dds.jetpack.room.dao.UserDao;
import com.dds.jetpack.room.entity.MessageBean;
import com.dds.jetpack.room.entity.UserInfo;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */

@Database(entities = {UserInfo.class, MessageBean.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    public abstract UserDao getUserDao();

    public abstract MessageDao getMessageDao();

}

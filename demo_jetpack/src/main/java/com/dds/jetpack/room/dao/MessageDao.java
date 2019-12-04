package com.dds.jetpack.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dds.jetpack.room.entity.MessageBean;

import java.util.Date;
import java.util.List;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */
@Dao
public interface MessageDao {

    @Query("Select * from message")
    List<MessageBean> getAll();

    @Query("Select * from message WHERE updateTime BETWEEN :from AND :to")
    List<MessageBean> getMsgBetweenDatas(Date from, Date to);

    @Insert
    void insertAll(MessageBean... users);

    @Delete
    void delete(MessageBean... users);

    @Update
    void update(MessageBean... users);
}

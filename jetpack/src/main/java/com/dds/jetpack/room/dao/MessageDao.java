package com.dds.jetpack.room.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dds.jetpack.room.entity.MessageBean;

import java.util.List;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */
public interface MessageDao {

    @Query("Select * from message")
    List<MessageBean> getAll();

    @Insert
    void insertAll(MessageBean... users);

    @Delete
    void delete(MessageBean... users);

    @Update
    void update(MessageBean... users);
}

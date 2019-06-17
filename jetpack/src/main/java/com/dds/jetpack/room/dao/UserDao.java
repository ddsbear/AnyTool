package com.dds.jetpack.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dds.jetpack.room.entity.UserInfo;

import java.util.List;

/**
 * 用户信息操作类
 */

@Dao
public interface UserDao {

    @Query("Select * from user_info")
    List<UserInfo> getAll();

    @Insert
    void insertAll(UserInfo... users);

    @Delete
    void delete(UserInfo... users);

    @Update
    void update(UserInfo... users);
}

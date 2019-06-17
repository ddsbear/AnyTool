package com.dds.jetpack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 用户体系
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */

@Entity(tableName = "user_info")
public class UserInfo {
    @PrimaryKey
    @ColumnInfo(name = "userId")
    private long userId;
    @ColumnInfo(name = "userName")
    private String userName;
    @ColumnInfo(name = "nickName")
    private String nickName;
    @ColumnInfo(name = "avatar")
    private String avatar;
    @ColumnInfo(name = "email")
    private String email;
    public UserInfo() {
    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

package com.utils.dddemo.db;

import com.dds.dbframwork.annotation.DbField;
import com.dds.dbframwork.annotation.DbTable;

/**
 * Created by dds on 2019/7/16.
 * android_shuai@163.com
 */

@DbTable("UserInfo")
public class UserInfo {
    @DbField("user_id")
    private Long userId;
    @DbField("user_name")
    private String userName;
    @DbField("nick_name")
    private String nickName;
    @DbField("avatar")
    private String avatar;
    @DbField("mark_name")
    private String markName;


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

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

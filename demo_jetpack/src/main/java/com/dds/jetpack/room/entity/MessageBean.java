package com.dds.jetpack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 消息实体类
 */


//@Entity(foreignKeys = {
//        @ForeignKey(entity = UserInfo.class, parentColumns = {"userId"}, childColumns = {"userId"}),
//        @ForeignKey(entity = UserInfo.class, parentColumns = {"userId"}, childColumns = {"fromId"})})
@Entity(tableName = "message")
public class MessageBean {
    @PrimaryKey
    @ColumnInfo(name = "msgId")
    private long msgId;
    @ColumnInfo(name = "userId")
    private long userId;
    @ColumnInfo(name = "fromId")
    private long fromId;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "mediaFilePath")
    private String mediaFilePath;
    @ColumnInfo(name = "updateTime")
    private long updateTime;
    @ColumnInfo(name = "msgType")
    private int msgType;


    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaFilePath() {
        return mediaFilePath;
    }

    public void setMediaFilePath(String mediaFilePath) {
        this.mediaFilePath = mediaFilePath;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}

package com.dds.jetpack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * 消息实体类
 */
@Entity(tableName = "message")
public class MessageBean {
    @ColumnInfo(name = "msgId")
    private long msgId;
    @ColumnInfo(name = "userId")
    private long userId;
    @ColumnInfo(name = "fromId")
    private long fromId;
    @ColumnInfo(name = "toId")
    private long toId;
    @ColumnInfo(name = "content")
    private String content;
    @ColumnInfo(name = "mediaFilePath")
    private String mediaFilePath;


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

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
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
}

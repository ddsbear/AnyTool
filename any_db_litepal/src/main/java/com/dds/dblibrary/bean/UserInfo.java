package com.dds.dblibrary.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Created by dds on 2019/6/14.
 * android_shuai@163.com
 */
public class UserInfo extends LitePalSupport {

    @Column(unique = true, nullable = false)
    private long userId;

    private String userName;
    private String nickName;
    private String avatar;
    private String remark;

}

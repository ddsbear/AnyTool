package com.dds.jetpack.room.utils;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Created by dds on 2019/6/17.
 * android_shuai@163.com
 */
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}

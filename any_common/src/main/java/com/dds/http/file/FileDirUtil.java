package com.dds.http.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class FileDirUtil {

    private FileDirUtil() {
    }

    /**
     * 沙箱 默认文件存储路径  data/data/file
     */
    public static String getFileDir(Context context) {
        return String.valueOf(context.getFilesDir());
    }

    /**
     * 沙箱 程序系统文件目录绝对路径 data/data/file/customPath
     *
     * @param context    context
     * @param customPath customPath
     * @return data/data/file/customPath
     */
    public static String getFileDir(Context context, String customPath) {
        String path = context.getFilesDir() + formatPath(customPath);
        mkdir(path);
        return path;
    }

    /**
     * @return 程序系统缓存目录
     */
    public static String getCacheDir(Context context) {
        return String.valueOf(context.getCacheDir());
    }

    /**
     * 程序系统缓存目录 中自定义路径
     *
     * @param context    上下文
     * @param customPath 自定义路径
     * @return /data/app/pkg/cache
     */
    public static String getCacheDir(Context context, String customPath) {
        String path = context.getCacheDir() + formatPath(customPath);
        mkdir(path);
        return path;
    }

    /**
     * @return 内存卡文件目录
     */
    public static String getExternalFileDir(Context context) {
        return String.valueOf(context.getExternalFilesDir(""));
    }

    /**
     * @param context    上下文
     * @param customPath 自定义路径
     * @return 内存卡文件目录
     */
    public static String getExternalFileDir(Context context, String customPath) {
        String path = context.getExternalFilesDir("") + formatPath(customPath);
        mkdir(path);
        return path;
    }

    /**
     * @return 内存卡缓存目录
     */
    public static String getExternalCacheDir(Context context) {
        return String.valueOf(context.getExternalCacheDir());
    }

    /**
     * @param context    上下文
     * @param customPath 自定义路径
     * @return 内存卡缓存目录
     */
    public static String getExternalCacheDir(Context context, String customPath) {
        String path = context.getExternalCacheDir() + formatPath(customPath);
        mkdir(path);
        return path;
    }

    /**
     * @return 公共下载文件夹
     */
    public static String getPublicDownloadDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    /**
     * 创建文件夹
     *
     * @param DirPath 文件夹路径
     */
    public static boolean mkdir(String DirPath) {
        File file = new File(DirPath);
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            return file.mkdirs();
        }


    }

    /**
     * 格式化文件路径
     * 示例：  传入 "sloop" "/sloop" "sloop/" "/sloop/"
     * 返回 "/sloop"
     */
    private static String formatPath(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        while (path.endsWith("/"))
            path = new String(path.toCharArray(), 0, path.length() - 1);
        return path;
    }

    /**
     * @return 存储卡是否挂载(存在)
     */
    public static boolean isMountSdcard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }
}

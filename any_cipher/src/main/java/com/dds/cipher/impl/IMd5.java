package com.dds.cipher.impl;


public interface IMd5 {
    /**
     * 一次 md5
     *
     * @param str
     * @return
     */
    String Md5(String str);

    /**
     * 兩次 md5值
     *
     * @param str
     * @return
     */
    String Md5Twice(String str);

    /**
     * 获取文件md5值
     * @param filePath 文件路徑
     * @return
     */
    String getFileMd5(String filePath);
}

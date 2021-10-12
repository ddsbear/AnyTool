package com.dds.cipher.impl;

/**
 * @Author: dongss
 * @CreateDate: 2021/10/12
 * ========================================
 * CopyRight (c) 2021 DDS.Co.Ltd.
 * All rights reserved.
 * <p>
 */
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

package com.dds.cipher.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dds on 2019/10/25.
 * android_shuai@163.com
 */
public class MD5Crypt {

    /**
     * md5 encrypt
     * @param b 需要加密在数据
     * @return md5 byte[]
     * @throws NoSuchAlgorithmException throw
     */
    public static byte[] md5(byte[] b) throws NoSuchAlgorithmException {
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest digest = MessageDigest.getInstance("MD5");
        // 使用指定的字节更新摘要
        digest.update(b);
        // 获得密文
        return digest.digest();
    }
}

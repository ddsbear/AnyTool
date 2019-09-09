package com.dds.cipher.javaImpl;

import com.dds.cipher.ByteUtil;
import com.dds.cipher.MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class JavaMd5 implements MD5 {

    @Override
    public String Md5(String str) {
        if (str.equals("")) {
            return null;
        }
        try {
            byte[] btInput = str.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            digest.update(btInput);
            // 获得密文
            byte[] md = digest.digest();
            return ByteUtil.byte2HexStr(md, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String Md5Twice(String str) {
        if (str.equals("")) {
            return null;
        }
        try {
            byte[] btInput = str.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest digest = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            digest.update(btInput);
            // 获得密文
            byte[] md = digest.digest();
            digest.update(md);
            byte[] md2 = digest.digest();
            return ByteUtil.byte2HexStr(md2, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}

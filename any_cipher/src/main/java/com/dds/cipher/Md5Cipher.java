package com.dds.cipher;

import com.dds.cipher.impl.IMd5;
import com.dds.cipher.md5.MD5Crypt;
import com.dds.cipher.util.ByteUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class Md5Cipher implements IMd5 {

    @Override
    public String Md5(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        try {
            byte[] btInput = str.getBytes();
            byte[] md = MD5Crypt.md5(btInput);
            return ByteUtil.byte2HexStr(md);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String Md5Twice(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        try {
            byte[] btInput = str.getBytes();
            // 获得密文
            byte[] md = MD5Crypt.md5(btInput);
            byte[] md2 = MD5Crypt.md5(md);
            return ByteUtil.byte2HexStr(md2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getFileMd5(String filePath) {
        InputStream fis;
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                mdInst.update(buffer, 0, numRead);
            }
            fis.close();
            return ByteUtil.byte2HexStr(mdInst.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

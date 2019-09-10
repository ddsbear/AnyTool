package com.dds.cipher.javaImpl;

import com.dds.cipher.AES;
import com.dds.cipher.aes.AESCrypt;

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Created by dds on 2019/9/3.
 * android_shuai@163.com
 */
public class JavaAES implements AES {

    private int length;

    public JavaAES(int length) {
        this.length = length;
    }


    @Override
    public String encText(String sSrc, String sKey) {
        try {
            return AESCrypt.encrypt(sKey, sSrc);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decText(String sSrc, String sKey, int length) {
        try {
            return AESCrypt.decrypt(sKey, sSrc);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long encFile(String inputFileUrl, String outFileUrl, String sKey) {
        return 0;
    }

    @Override
    public InputStream decFile(String inputFileUrl, String sKey, int length) {
        return null;
    }

    @Override
    public byte[] AesEncByte(String key, byte[] data, int needPad) {
        return new byte[0];
    }

    @Override
    public byte[] AesDecByte(String key, byte[] encData, int hasPad) {
        return new byte[0];
    }

    @Override
    public String generateRandomCharAndNumber() {
        return null;
    }

    @Override
    public void setKeyLength(int length) {
        this.length = length;

    }
}

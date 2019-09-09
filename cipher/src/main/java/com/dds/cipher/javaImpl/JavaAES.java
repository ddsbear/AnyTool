package com.dds.cipher.javaImpl;

import com.dds.cipher.AES;

import java.io.InputStream;

/**
 * Created by dds on 2019/9/3.
 * android_shuai@163.com
 */
public class JavaAES implements AES {

    @Override
    public String encryptText(String sSrc, String sKey) {
        return null;
    }

    @Override
    public String decryptText(String sSrc, String sKey, int length) {
        return null;
    }

    @Override
    public long encryptFile(String inputFileUrl, String outFileUrl, String sKey) {
        return 0;
    }

    @Override
    public InputStream decryptFile(String inputFileUrl, String sKey, int length) {
        return null;
    }

    @Override
    public byte[] AesEncryptByte(String key, byte[] data, int needPad) {
        return new byte[0];
    }

    @Override
    public byte[] AesDecryptByte(String key, byte[] encData, int hasPad) {
        return new byte[0];
    }

    @Override
    public String generateRandomCharAndNumber() {
        return null;
    }

    @Override
    public void setKeyLength(int length) {

    }
}

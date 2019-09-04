package com.dds.cipher;

import java.io.InputStream;

/**
 * Created by dds on 2018/5/30.
 * android_shuai@163.com
 */

public interface AES {

    String encryptText(String sSrc, String sKey);

    String decryptText(String sSrc, String sKey, int length);

    long encryptFile(String inputFileUrl, String outFileUrl, String sKey);

    InputStream decryptFile(String inputFileUrl, String sKey, int length);

    byte[] AesEncryptByte(String key, byte[] data, int needPad);

    byte[] AesDecryptByte(String key, byte[] encData, int hasPad);

    String generateRandomCharAndNumber();

    void setKeyLength(int length);
}

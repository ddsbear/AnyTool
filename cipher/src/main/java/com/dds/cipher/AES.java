package com.dds.cipher;

import java.io.InputStream;

/**
 * Created by dds on 2018/5/30.
 * android_shuai@163.com
 */

public interface AES {

    String encText(String sSrc, String sKey);

    String decText(String sSrc, String sKey, int length);

    long encFile(String inputFileUrl, String outFileUrl, String sKey);

    InputStream decFile(String inputFileUrl, String sKey, int length);

    byte[] AesEncByte(String key, byte[] data, int needPad);

    byte[] AesDecByte(String key, byte[] encData, int hasPad);

    String generateRandomCharAndNumber();

    void setKeyLength(int length);
}

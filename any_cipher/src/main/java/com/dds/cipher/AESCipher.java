package com.dds.cipher;

import com.dds.cipher.aes.AESCrypt;
import com.dds.cipher.impl.IAes;

import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * Created by dds on 2019/9/3.
 * android_shuai@163.com
 */
public class AESCipher implements IAes {

    @Override
    public String encText(String sSrc, String sKey) {
        try {
            return AESCrypt.encrypt(sKey, sSrc,
                    false, null,
                    "IAes/ECB/NoPadding", null);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decText(String sSrc, String sKey, int length) {
        try {

            return AESCrypt.decrypt(sKey, sSrc,
                    false, null,
                    "IAes/ECB/NoPadding", null);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long encFile(String inputFile, String outFile, String sKey) {
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


}

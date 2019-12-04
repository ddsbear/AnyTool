package com.dds.cipher;

import com.dds.cipher.aes.AES;
import com.dds.cipher.impl_java.JavaAES;
import com.dds.cipher.impl_java.JavaMd5;
import com.dds.cipher.impl_java.JavaRSA;
import com.dds.cipher.md5.MD5;
import com.dds.cipher.rsa.RSA;

/**
 * Created by dds on 2019/10/25.
 * android_shuai@163.com
 */
public class Cipher {
    private static AES mAes;
    private static RSA mRsa;
    private static MD5 mMd5;

    public static void initDefault() {
        mAes = new JavaAES(32);
        mRsa = new JavaRSA(2048);
        mMd5 = new JavaMd5();
    }

    // ------------------AES--------------------------


    // ------------------RSA--------------------------


    // ------------------sha256-----------------------


    // ------------------SM4----AES----------------------


    // ------------------SM2-----RSA---------------------


    // ------------------SM3------sha256------------------


    // ------------------MD5--------------------------


}

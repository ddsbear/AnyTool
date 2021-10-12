package com.dds.cipher;

import com.dds.cipher.impl.JavaMd5;
import com.dds.cipher.impl.RSACipher;
import com.dds.cipher.md5.MD5;
import com.dds.cipher.impl.IRsa;

/**
 * Created by dds on 2019/10/25.
 * android_shuai@163.com
 */
public class Cipher {

    private static IRsa mRsa;
    private static MD5 mMd5;

    public static void initDefault() {
        mRsa = new RSACipher(2048);
        mMd5 = new JavaMd5();
    }

    // ------------------IAes--------------------------


    // ------------------IRsa--------------------------


    // ------------------sha256-----------------------


    // ------------------SM4----IAes----------------------


    // ------------------SM2-----IRsa---------------------


    // ------------------SM3------sha256------------------


    // ------------------MD5--------------------------


}

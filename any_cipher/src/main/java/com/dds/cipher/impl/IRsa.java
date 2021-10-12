package com.dds.cipher.impl;

/**
 * Created by dds on 2018/5/30.
 * android_shuai@163.com
 * <p>
 * RSA加密接口
 */

public interface IRsa {

    KeyPair generateAsymmetricKey();

    String encByPubKey(String content, String pubKey);

    String decByPriKey(String content, String priKey, String pwd);

    class KeyPair {
        public KeyPair(String priKey, String pubKey) {
            this.priKey = priKey;
            this.pubKey = pubKey;
        }

        String priKey;
        String pubKey;

    }

}

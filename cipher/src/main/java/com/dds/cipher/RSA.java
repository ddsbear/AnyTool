package com.dds.cipher;

/**
 * Created by dds on 2018/5/30.
 * android_shuai@163.com
 * <p>
 * RSA加密接口
 */

public interface RSA {

    KeyPair generateAsymmetricKey();

    String encByPubKey(String content, String pubKey,String pwd);

    String decByPriKey(String content, String priKey, String pwd);

    public class KeyPair {
        public KeyPair(String priKey, String pubKey) {
            this.priKey = priKey;
            this.pubKey = pubKey;
        }

        public String priKey;
        public String pubKey;

    }

}

package com.dds.cipher;

import com.dds.cipher.base64.Base64;
import com.dds.cipher.impl.IRsa;
import com.dds.cipher.rsa.RSACrypt;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * Created by dds on 2019/9/3.
 * android_shuai@163.com
 */
public class RSACipher implements IRsa {

    private int length;

    public RSACipher(int length) {
        this.length = length;
    }

    @Override
    public KeyPair generateAsymmetricKey() {
        try {
            java.security.KeyPair keys = RSACrypt.generateKeys();
            String priKey = new String(Base64.encode(keys.getPrivate().getEncoded()));
            String pubKey = new String(Base64.encode(keys.getPublic().getEncoded()));
            return new KeyPair(priKey, pubKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String encByPubKey(String content, String pubKeyCrt) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pubKeyCrt.getBytes()));
            PublicKey publicKey = cert.getPublicKey();
            String pubkey = new String(Base64.encode(publicKey.getEncoded()));
            return RSACrypt.encByPub(content, pubkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String decByPriKey(String content, String P12, String eum) {
        try {
            // 1. 解密base64
            byte[] decode = Base64.decode(P12.getBytes());

            // 2. 读取
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ByteArrayInputStream bis = new ByteArrayInputStream(decode);
            char[] pwd = (eum != null && !eum.equals("")) ? eum.toCharArray() : null;
            ks.load(bis, pwd);
            bis.close();

            Enumeration enums = ks.aliases();
            String keyAlias = null;
            if (enums.hasMoreElements()) // we are readin just one certificate.
            {
                keyAlias = (String) enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));

            //3.拿出私钥
            PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, pwd);

            String priStr = new String(Base64.encode(privateKey.getEncoded()));


            return RSACrypt.decByPri(content, priStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

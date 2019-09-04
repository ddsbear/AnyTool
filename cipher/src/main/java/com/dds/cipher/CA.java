package com.dds.cipher;


import com.dds.cipher.base64.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.security.auth.x500.X500Principal;

public class CA {

    private static final String publicKeyFileName = System.getProperty("user.dir") + File.separator + "pubkey.cer";
    private static final String privateKeyFileName = System.getProperty("user.dir") + File.separator + "private.pfx";
    private static final String pfxPassword = "123";//私钥文件获取时设置的密钥
    private static String aliasName = "003";//alias名称

    public CA() {
        Security.addProvider(new BouncyCastleProvider());
    }


    // 簽名
    public static byte[] sign(String str) {
        byte[] base64Sign = null;
        InputStream fis = null;
        try {
            fis = new FileInputStream(privateKeyFileName);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] pscs = pfxPassword.toCharArray();
            keyStore.load(fis, pscs);
            PrivateKey priKey = (PrivateKey) (keyStore.getKey(aliasName, pscs));
            // 签名
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(priKey);
            byte[] bysData = str.getBytes("UTF-8");
            sign.update(bysData);
            byte[] signByte = sign.sign();
            base64Sign = Base64.encode(signByte);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64Sign;
    }

    // 驗證
    public static boolean verify(String signStr, String verStr)
            throws Exception {
        boolean verfy = false;
        InputStream fis = null;
        try {
            fis = new FileInputStream(publicKeyFileName);
            CertificateFactory cf = CertificateFactory.getInstance("x509");
            Certificate cerCert = cf.generateCertificate(fis);
            PublicKey pubKey = cerCert.getPublicKey();
            String signed = Base64.decode(signStr);
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify(pubKey);
            sign.update(verStr.getBytes("UTF-8"));
            verfy = sign.verify(signed.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return verfy;
    }

    // 加密
    public static byte[] encrypt(String source) throws Exception {
        InputStream fis = null;
        try {
            fis = new FileInputStream(publicKeyFileName);
            CertificateFactory cf = CertificateFactory.getInstance("x509");
            Certificate cerCert = cf.generateCertificate(fis);
            PublicKey pubKey = cerCert.getPublicKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] sbt = source.getBytes();
            byte[] epByte = cipher.doFinal(sbt);
            byte[] epStr;
            epStr = Base64.encode(epByte, 1);

            return epStr;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 解密
    public static String decode(String source) throws Exception {
        String keyByte = Base64.decode(source);
        InputStream fis = null;
        try {
            fis = new FileInputStream(privateKeyFileName);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] pscs = pfxPassword.toCharArray();
            keyStore.load(fis, pscs);
            PrivateKey priKey = (PrivateKey) (keyStore.getKey(aliasName, pscs));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] epByte = cipher.doFinal(keyByte.getBytes());
            return new String(epByte, "UTF-8");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 生成KeyPair
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();

    }

    // 生成p10证书
    public static String generatePKCS10(KeyPair keyPair) throws Exception {
        String sigAlg = "SHA256withRSA";
        String params = "CN = CN," + "OU = BJ," + "O = BJ";
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Principal(params), keyPair.getPublic());// CN和公钥
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(sigAlg);// 签名算法
        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        PKCS10CertificationRequest p10 = p10Builder.build(signer);
        String str = new String(Base64.encode(p10.getEncoded()));
        System.out.println(str);
        return str;
    }

    // 合成p12证书
    public static void storeP12(PrivateKey pri, String p7, String p12Path, String p12Password) throws Exception {
        Certificate[] chain = new Certificate[2];
        CertificateFactory factory = CertificateFactory.getInstance("X509");
        X509Certificate priX509 = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(pri.getEncoded()));
        X509Certificate p7X509 = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(p7.getBytes()));
        chain[0] = priX509;
        chain[1] = p7X509;
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(null, null);
        ks.setKeyEntry(parseCertDN(p7X509.getSubjectDN().getName(), "CN"),
                pri, null, chain);
        FileOutputStream fOut = new FileOutputStream(p12Path);
        ks.store(fOut, p12Password.toCharArray());
    }

    public static String parseCertDN(String dn, String type) {
        type = type + "=";
        String[] split = dn.split(",");
        for (String x : split) {
            if (x.contains(type)) {
                x = x.trim();
                return x.substring(type.length());
            }
        }
        return null;
    }


}
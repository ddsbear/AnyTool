package com.dds.cipher;


import android.os.Environment;

import com.dds.cipher.base64.Base64;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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
        // 证书签名算法
        String sigAlg = "SHA256withRSA";
        // 各种基本信息
        String params = "CN=CN,OU=Trustmobi,O=client3,L=BJ2";

        // CN和公钥
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Name(params), keyPair.getPublic());
        // 签名算法
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(sigAlg);
        csBuilder.setProvider(new BouncyCastleProvider());

        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        // 生成PKCS10的二进制编码格式(ber/der)
        PKCS10CertificationRequest p10 = p10Builder.build(signer);

        //将二进制格式转换为证书格式(csr)
        PemObject pemObject = new PemObject("CERTIFICATE REQUEST", p10.getEncoded());
        StringWriter str = new StringWriter();
        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(str);
        jcaPEMWriter.writeObject(pemObject);
        jcaPEMWriter.close();
        str.close();
        // base64便于网络传输
        return Base64.encode(str.toString());
    }


    public static void write(String content) throws IOException {
        //创建一个带缓冲区的输出流
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File SDPath = Environment.getExternalStorageDirectory();//SD根目录
            File file = new File(SDPath, "data.txt");
            FileWriter writer = new FileWriter(file, true);
            writer.write(content);
            writer.close();
        }
    }

    // 合成p12证书
    public static void storeP12(PrivateKey pri, String p7, String p12Path, String p12Password) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X509");
        X509Certificate p7X509 = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(p7.getBytes()));
        Certificate[] chain = new Certificate[]{p7X509};
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(null, null);
        ks.setKeyEntry("private", pri, p12Password.toCharArray(), chain);
        FileOutputStream fOut = new FileOutputStream(p12Path);
        ks.store(fOut, p12Password.toCharArray());
    }


}
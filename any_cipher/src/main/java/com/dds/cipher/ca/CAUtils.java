package com.dds.cipher.ca;


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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CAUtils {

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

    // 合成p12证书
    public static void storeP12(PrivateKey pri, String p7, String p12Path, String p12Password) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X509");
        //初始化证书链
        X509Certificate p7X509 = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(p7.getBytes()));
        Certificate[] chain = new Certificate[]{p7X509};
        // 生成一个空的p12证书
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(null, null);
        // 将服务器返回的证书导入到p12中去
        ks.setKeyEntry("client", pri, p12Password.toCharArray(), chain);
        // 加密保存p12证书
        FileOutputStream fOut = new FileOutputStream(p12Path);
        ks.store(fOut, p12Password.toCharArray());
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


}
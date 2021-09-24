package com.dds.cipher.rsa;


import androidx.annotation.Nullable;

import com.dds.cipher.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSACrypt {

    //加密算法RSA
    private static final String KEY_ALGORITHM = "RSA";

    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private static final int keySize = 2048;

    public static final String CHARSET = "UTF-8";


    /**
     * 生成公钥和私钥
     *
     * @throws Exception Exception
     */
    public static KeyPair generateKeys() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        return keyPairGen.generateKeyPair();
    }

    /**
     * 公钥加密
     *
     * @param data data
     * @return String
     * @throws RuntimeException Exception
     */
    public static String encByPub(String data, String public_key) throws RuntimeException {
        try {
            byte[] dataByte = data.getBytes();
            byte[] keyBytes = Base64.decode(public_key.getBytes());
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            return new String(Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, dataByte, keySize)));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param data data
     * @return String
     * @throws RuntimeException Exception
     */
    public static String decByPri(String data, String private_key) throws RuntimeException {
        try {
            byte[] keyBytes = Base64.decode(private_key.getBytes());
            byte[] dataBytes = Base64.decode(data.getBytes());
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, dataBytes, keySize),
                    CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 签名
     *
     * @param data       待签名数据
     * @param privateKey 私钥
     * @return 签名
     */
    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(Base64.encode(signature.sign()));
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     * @return 是否验签通过
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(key);
        signature.update(srcData.getBytes());
        return signature.verify(Base64.decode(sign.getBytes()));
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opcodes, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opcodes == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        closeQuietly(out);
        return resultDatas;
    }

    private static void closeQuietly(final @Nullable Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (final RuntimeException e) {
            throw e;
        } catch (final Exception ignored) {
        }
    }

}

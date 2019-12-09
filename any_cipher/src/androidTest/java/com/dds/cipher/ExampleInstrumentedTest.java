package com.dds.cipher;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dds.cipher.aes.AESCrypt;
import com.dds.cipher.base64.Base64;
import com.dds.cipher.rsa.RSACrypt;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private final static String TAG = "dds_test";
    private static final byte[] ivBytes = {
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.dds.cipher.test", appContext.getPackageName());
    }


    @Test
    public void testAESECBNoPadding() throws Exception {
        String content = "欢迎来到 any_cipher";
        String key = "123456";
        //========================NoPadding test =================================
        String encrypt = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "AES/ECB/NoPadding", null);

        Log.d(TAG, "encrypt result-------------->：" + encrypt);

        String result = AESCrypt.decrypt(key, encrypt,
                true, "SHA-256",
                "AES/ECB/NoPadding", null);

        Log.d(TAG, "decrypt result-------------->：" + result);

        assertEquals(result.trim(), content);


    }

    @Test
    public void testAESCBCPKCS5Padding() throws Exception {
        //========================PKCS5Padding ==================================
        String content = "欢迎来到any_cipher";
        String key = "123456";
        String encrypt1 = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "AES/CBC/PKCS5Padding", ivBytes);


        Log.d(TAG, "result------------>：" + encrypt1);

        String result1 = AESCrypt.decrypt(key, encrypt1,
                true, "SHA-256",
                "AES/CBC/PKCS5Padding", ivBytes);

        Log.d(TAG, "result------------>：" + result1);

        assertEquals(result1.trim(), content);
        //========================PKCS7Padding test ==================================

        // 1. pkcs7padding和pkcs5padding的区别在于pkcs7padding的块大小不固定

        String encrypt2 = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "AES/ECB/PKCS7Padding", null);


        Log.d(TAG, "加密出内容：" + encrypt2);

        String result2 = AESCrypt.decrypt(key, encrypt2,
                true, "SHA-256",
                "AES/ECB/PKCS7Padding", null);

        Log.d(TAG, "解密出内容：" + result2);

        assertEquals(result2.trim(), content);

    }

    @Test
    public void testAESFile() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        File srcFile = new File(appContext.getFilesDir(), "test.txt");
        // 创建一个文件并写入内容
        Utils.writeFile("hello world", srcFile.getAbsolutePath(), false);
        String key = "123456";
        File encDir = new File(appContext.getFilesDir(), "a_enc");
        if (!encDir.exists()) {
            encDir.mkdirs();
        }
        String s = AESCrypt.encryptFile(key, srcFile.getAbsolutePath(), encDir.getAbsolutePath(),
                true, "SHA-256", "AES/ECB/PKCS5Padding", null);
        if (s != null) {
            String s1 = Utils.readFile(s);
            Log.d(TAG, "enc result------>" + s1);
        }
        File decDir = new File(appContext.getFilesDir(), "a_dec");
        if (!decDir.exists()) {
            decDir.mkdirs();
        }
        String s2 = AESCrypt.decryptFile(key, s, decDir.getAbsolutePath(),
                true, "SHA-256", "AES/ECB/PKCS5Padding", null);
        if (s2 != null) {
            String s3 = Utils.readFile(s2);
            Log.d(TAG, "dec result------>" + s3);
        }
    }

    @Test
    public void testAES2() throws Exception {
        String content = "123456";
        String key = "GAOQXQQ99QPKOMTZE9YF96OLTD8EU6T9";


        String encrypt = AESCrypt.encrypt(key, content,
                false, null,
                "AES/ECB/PKCS5Padding", null);
        Log.d(TAG, "加密出内容：" + encrypt);

        String decrypt = AESCrypt.decrypt(key, content,
                false, null,
                "AES/ECB/PKCS5Padding", null);
        Log.d(TAG, "解密出内容：" + decrypt);

        assertEquals("123456", decrypt.trim());


    }

    @Test
    public void testRSA() throws Exception {
        String content = "123456";
        // 生成公私钥
        KeyPair keyPair = RSACrypt.generateKeys();
        PrivateKey aPrivate = keyPair.getPrivate();
        PublicKey aPublic = keyPair.getPublic();
        String pubkey = new String(Base64.encode(aPublic.getEncoded()));
        // 用公钥加密
        String encStr = RSACrypt.encByPub(content, pubkey);
        Log.d(TAG, "encStr---------> " + encStr);
        String priKey = new String(Base64.encode(aPrivate.getEncoded()));
        // 用私钥解密
        String decStr = RSACrypt.decByPri(encStr, priKey);
        Log.d(TAG, "decStr---------> " + decStr);
    }

    @Test
    public void testBase64() {
        String src = "ddssingsong";

        String encode = Base64.encode(src);
        Log.d(TAG, "base64 enc:" + encode);
        String decode = Base64.decode(encode);
        Log.d(TAG, "base64 dec:" + decode);

    }

}

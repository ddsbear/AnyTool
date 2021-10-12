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
    public void testAES2() throws Exception {
        String content = "123456";
        String key = "GAOQXQQ99QPKOMTZE9YF96OLTD8EU6T9";


        String encrypt = AESCrypt.encrypt(key, content,
                false, null,
                "IAes/ECB/PKCS5Padding", null);
        Log.d(TAG, "加密出内容：" + encrypt);

        String decrypt = AESCrypt.decrypt(key, content,
                false, null,
                "IAes/ECB/PKCS5Padding", null);
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

package com.dds.cipher;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dds.cipher.aes.AESCrypt;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.security.GeneralSecurityException;


@RunWith(AndroidJUnit4.class)
public class CipherAesTest {

    private final static String TAG = "dds_test";

    /**
     * password非16的倍数会抛出如下异常,需要设置digest为true对key进行处理SHA-256处理
     * java.security.InvalidKeyException: Unsupported key size: 8 bytes
     *
     * @throws GeneralSecurityException GeneralSecurityException
     */

    @Test
    public void testAESECBNoPaddingDigestKey() throws GeneralSecurityException {

        String content = "欢迎来到 any_cipher";
        String key = "12345678";

        String encrypt = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "IAes/ECB/NoPadding", null);

        Log.d(TAG, "encrypt result-------------->：" + encrypt);

        String result = AESCrypt.decrypt(key, encrypt,
                true, "SHA-256",
                "IAes/ECB/NoPadding", null);

        Log.d(TAG, "decrypt result-------------->：" + result);

        assertEquals(result.trim(), content);


    }

    /**
     * CBC模式是前后相关联的加密方式，离散性更好
     * 偏移量iv必须是16的倍数
     * PKCS5Padding自动补位，NoPadding需要手动补空格
     *
     * @throws GeneralSecurityException GeneralSecurityException
     */
    @Test
    public void testAESCBCPKCS5PaddingDigestKey() throws Exception {


        String content = "欢迎来到any_cipher";
        String key = "12345678";
        String iv = "1111111111111111";
        String encrypt1 = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "IAes/CBC/PKCS5Padding", iv.getBytes());

        Log.d(TAG, "enc result------------>：" + encrypt1);

        String result1 = AESCrypt.decrypt(key, encrypt1,
                true, "SHA-256",
                "IAes/CBC/PKCS5Padding", iv.getBytes());

        Log.d(TAG, "dec result------------>：" + result1);

        assertEquals(result1.trim(), content);


    }

    /**
     * pkcs7padding和pkcs5padding的区别在于pkcs7padding的块大小不固定
     *
     * @throws GeneralSecurityException GeneralSecurityException
     */
    @Test
    public void testAESCBCPKCS7PaddingDigestKey() throws Exception {

        String content = "欢迎来到any_cipher";
        String key = "12345678";
        String encrypt2 = AESCrypt.encrypt(key, content,
                true, "SHA-256",
                "IAes/ECB/PKCS7Padding", null);

        Log.d(TAG, "enc result------------>：：" + encrypt2);

        String result2 = AESCrypt.decrypt(key, encrypt2,
                true, "SHA-256",
                "IAes/ECB/PKCS7Padding", null);

        Log.d(TAG, "dec result------------>：：" + result2);

        assertEquals(result2.trim(), content);

    }


    @Test
    public void testAESNoDigestKey() throws Exception {
        /*
         * digest为false时，key的长度必须为16的倍数
         */
        String content = "1234567890123456";
        String key = "1234567890123456";


        String encrypt = AESCrypt.encrypt(key, content,
                false, null,
                "IAes/ECB/PKCS5Padding", null);
        Log.d(TAG, "加密出内容：" + encrypt);

        String decrypt = AESCrypt.decrypt(key, encrypt,
                false, null,
                "IAes/ECB/PKCS5Padding", null);
        Log.d(TAG, "解密出内容：" + decrypt);

        assertEquals(content, decrypt.trim());


    }

    @Test
    public void testAESFile() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String content = "hello world my name is dds";
        File srcFile = new File(appContext.getFilesDir(), "test.txt");
        // 创建一个文件并写入内容
        Utils.writeFile(content, srcFile.getAbsolutePath(), false);
        String key = "123456";
        File encDir = new File(appContext.getFilesDir(), "a_enc");
        if (!encDir.exists()) {
            encDir.mkdirs();
        }
        String s = AESCrypt.encryptFile(key, srcFile.getAbsolutePath(), encDir.getAbsolutePath(),
                true, "SHA-256", "IAes/ECB/PKCS5Padding", null);
        if (s != null) {
            String s1 = Utils.readFile(s);
            Log.d(TAG, "enc result------>：" + s1);
        }
        File decDir = new File(appContext.getFilesDir(), "a_dec");
        if (!decDir.exists()) {
            decDir.mkdirs();
        }
        String s2 = AESCrypt.decryptFile(key, s, decDir.getAbsolutePath(),
                true, "SHA-256", "IAes/ECB/PKCS5Padding", null);
        String s3 = "";
        if (s2 != null) {
            s3 = Utils.readFile(s2);
            Log.d(TAG, "dec result------>：" + s3);
        }

        assertEquals(s3, content);


    }
}

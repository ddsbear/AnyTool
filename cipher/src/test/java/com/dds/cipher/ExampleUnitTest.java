package com.dds.cipher;

import com.dds.cipher.javaImpl.JavaAES;
import com.dds.cipher.javaImpl.JavaMd5;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        // assertEquals(4, 2 + 2);

        JavaMd5 javaMd5 = new JavaMd5();
        String s = javaMd5.Md5("111111");

        System.out.println("md5 16:" + s.substring(8, 24));
        System.out.println("md5 32:" + s);


        String s2 = javaMd5.Md5Twice("123456");
        System.out.println("md5 twice 16:" + s2.substring(8, 24));
        System.out.println("md5 twice 32:" + s2);
    }

    @Test
    public void aes() {

        AES cipher = new JavaAES(32);

        String text = "哈哈哈";
        String key = "123456";
        String s = cipher.encText(text, key);
        System.out.println("src:" + text + ",key：" + key);
        System.out.println("加密 result:" + s);

        String s1 = cipher.decText(s, key, 32);
        System.out.println("解密 result:" + s1);
    }
}
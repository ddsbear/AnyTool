package com.dds.cipher;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.dds.cipher.test", appContext.getPackageName());
    }



    @Test
    public void testAES() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String content = "123456";
        String key = "12345678901234567890123456789012";







        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        String filePath = "";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        CipherInputStream inputStream = new CipherInputStream(fileInputStream, cipher);

    }
}

package com.dds.http;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.dds.http.file.FileDirUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = "dds_test";

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.trustmobi.myapplication", appContext.getPackageName());
    }

    @Test
    public void testFileDirUtil() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String fileDir = FileDirUtil.getFileDir(appContext);
        Log.d(TAG, "getFileDir:" + fileDir);
        assertEquals(fileDir, "/data/user/0/com.utils.library.test/files");

        String fileDirCustom = FileDirUtil.getFileDir(appContext, "test");
        Log.d(TAG, "getFileDir:" + fileDirCustom);
        assertEquals(fileDirCustom, "/data/user/0/com.utils.library.test/files/test");

        String cacheDir = FileDirUtil.getCacheDir(appContext);
        Log.d(TAG, "getCacheDir:" + cacheDir);

        String cacheDir1 = FileDirUtil.getCacheDir(appContext, "test");
        Log.d(TAG, "getCacheDir:" + cacheDir1);

        String externalFileDir = FileDirUtil.getExternalFileDir(appContext);
        Log.d(TAG, "getExternalFileDir:" + externalFileDir);
        assertEquals(externalFileDir, "/storage/emulated/0/Android/data/com.utils.library.test/files");

        String externalFileDir1 = FileDirUtil.getExternalFileDir(appContext, "test");
        Log.d(TAG, "getExternalFileDir:" + externalFileDir1);


        String externalCacheDir = FileDirUtil.getExternalCacheDir(appContext);
        Log.d(TAG, "getExternalFileDir:" + externalCacheDir);

        String externalCacheDir1 = FileDirUtil.getExternalCacheDir(appContext, "test");
        Log.d(TAG, "getExternalFileDir:" + externalCacheDir1);


        String publicDownloadDir = FileDirUtil.getPublicDownloadDir();
        Log.d(TAG, "getPublicDownloadDir:" + publicDownloadDir);


        boolean is = FileDirUtil.isMountSdcard();
        Log.d(TAG, "isMountSdcard:" + is);
        assertTrue(is);


    }


}

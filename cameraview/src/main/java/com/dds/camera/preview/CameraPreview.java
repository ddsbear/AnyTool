package com.dds.camera.preview;

import androidx.annotation.NonNull;

/**
 * Created by dds on 2019/7/2.
 * android_shuai@163.com
 */
public class CameraPreview {
    private SurfaceCallback mSurfaceCallback;


    public final void setSurfaceCallback(@NonNull SurfaceCallback callback) {
        mSurfaceCallback = callback;
    }


    public interface SurfaceCallback {


        void onSurfaceAvailable();


        void onSurfaceChanged();


        void onSurfaceDestroyed();
    }
}

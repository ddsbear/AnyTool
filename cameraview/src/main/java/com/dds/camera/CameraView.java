package com.dds.camera;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.dds.camera.engine.Camera1Engine;
import com.dds.camera.engine.CameraEngine;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by dds on 2019/7/2.
 * android_shuai@163.com
 */
public class CameraView extends FrameLayout {
    private CameraEngine mCameraEngine;
    private CameraCallbacks mCameraCallbacks;

    private List<CameraListener> mListeners = new CopyOnWriteArrayList<>();

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // region Init
    public void init() {
        // 保证onDraw方法执行
        setWillNotDraw(false);

        // Components

        mCameraCallbacks = new CameraCallbacks();
        mCameraEngine = instantiateCameraController(mCameraCallbacks);


    }


    /**
     * 配置 Camera
     */
    @NonNull
    protected CameraEngine instantiateCameraController(@NonNull CameraEngine.Callback callback) {
        return new Camera1Engine(callback);
    }


    /**
     * 相机的回调
     */
    class CameraCallbacks implements CameraEngine.Callback {

        @Override
        public void dispatchOnCameraOpened(CameraOptions options) {

        }

        @Override
        public void dispatchOnCameraClosed() {

        }

        @Override
        public void onCameraPreviewStreamSizeChanged() {

        }

        @Override
        public void onShutter(boolean shouldPlaySound) {

        }

        @Override
        public void dispatchOnFocusStart(@Nullable Gesture trigger, @NonNull PointF where) {

        }

        @Override
        public void dispatchOnFocusEnd(@Nullable Gesture trigger, boolean success, @NonNull PointF where) {

        }

        @Override
        public void dispatchOnZoomChanged(float newValue, @Nullable PointF[] fingers) {

        }

        @Override
        public void dispatchOnExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {

        }

        @Override
        public void dispatchError(CameraException exception) {

        }
    }
}

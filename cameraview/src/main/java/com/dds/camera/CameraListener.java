package com.dds.camera;

import android.graphics.PointF;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;


public abstract class CameraListener {


    /**
     * 通知相机打开
     * The {@link CameraOptions} 查看有哪些配置
     *
     * @param options camera supported options
     */
    @UiThread
    public void onCameraOpened(@NonNull CameraOptions options) {

    }

    /**
     * 通知相机关闭
     */
    @UiThread
    public void onCameraClosed() {
    }

    /**
     * 报错
     * {@link CameraException#getReason()}
     *
     * @param exception
     */
    @UiThread
    public void onCameraError(@NonNull CameraException exception) {
    }


    /**
     * 拍照 结果
     *
     * @param result
     */
    @UiThread
    public void onPictureTaken(@NonNull PictureResult result) {
    }


    /**
     * 录像 结果
     *
     * @param result
     */
    @UiThread
    public void onVideoTaken(@NonNull VideoResult result) {
    }


    /**
     * 方向改变
     *
     * @param orientation
     */
    @UiThread
    public void onOrientationChanged(int orientation) {
    }

    /**
     * 对焦开始
     *
     * @param point
     */

    @UiThread
    public void onAutoFocusStart(@NonNull PointF point) {
    }


    /**
     * 对焦结束
     *
     * @param successful
     * @param point
     */
    @UiThread
    public void onAutoFocusEnd(boolean successful, @NonNull PointF point) {
    }


    /**
     * 预览 大小改变
     *
     * @param newValue
     * @param bounds
     * @param fingers
     */
    @UiThread
    public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
    }


    /**
     * 曝光度改变
     *
     * @param newValue
     * @param bounds
     * @param fingers
     */
    @UiThread
    public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
    }

}
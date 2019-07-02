package com.dds.camera.engine;

import android.gesture.Gesture;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import com.dds.camera.CameraException;
import com.dds.camera.CameraLogger;
import com.dds.camera.CameraOptions;
import com.dds.camera.preview.CameraPreview;
import com.dds.camera.utils.WorkerHandler;

/**
 * Created by dds on 2019/7/2.
 * android_shuai@163.com
 */
public abstract class CameraEngine implements CameraPreview.SurfaceCallback, Thread.UncaughtExceptionHandler {
    private final Callback mCallback;
    private CameraPreview mPreview;
    private Handler mCrashHandler;

    private WorkerHandler mHandler;  // 开启线程工作


    protected CameraOptions mCameraOptions;

    // log
    private static final String TAG = CameraEngine.class.getSimpleName();
    private static final CameraLogger LOG = CameraLogger.create(TAG);

    public CameraEngine(Callback callback) {
        this.mCallback = callback;
        mCrashHandler = new Handler(Looper.getMainLooper());
        mHandler = WorkerHandler.get("CameraViewController");
        mHandler.getThread().setUncaughtExceptionHandler(this);

    }

    public void setPreview(@NonNull CameraPreview cameraPreview) {
        mPreview = cameraPreview;
        mPreview.setSurfaceCallback(this);
    }

    //region destroy

    public void destroy() {
        LOG.i("destroy:", "state:", ss());
        // Prevent CameraEngine leaks. Don't set to null, or exceptions
        // inside the standard stop() method might crash the main thread.
        mHandler.getThread().setUncaughtExceptionHandler(new NoOpExceptionHandler());
        // Stop if needed.
        stopImmediately();
    }

    //endregion

    // region ------------ 开启&&关闭--------------

    // 状态
    protected int mState = STATE_STOPPED;
    static final int STATE_STOPPING = -1; // Camera is about to be stopped.
    public static final int STATE_STOPPED = 0; // Camera is stopped.
    static final int STATE_STARTING = 1; // Camera is about to start.
    public static final int STATE_STARTED = 2; // Camera is available and we can set parameters.

    @NonNull
    private String ss() {
        switch (mState) {
            case STATE_STOPPING:
                return "STATE_STOPPING";
            case STATE_STOPPED:
                return "STATE_STOPPED";
            case STATE_STARTING:
                return "STATE_STARTING";
            case STATE_STARTED:
                return "STATE_STARTED";
        }
        return "null";
    }

    //开始预览
    public final void start() {
        LOG.i("Start:", "posting runnable. State:", ss());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LOG.i("Start:", "executing. State:", ss());
                // 如果已经开启，返回
                if (mState >= STATE_STARTING) return;
                mState = STATE_STARTING;
                LOG.i("Start:", "about to call onStart()", ss());
                onStart();
                LOG.i("Start:", "returned from onStart().", "Dispatching.", ss());
                mState = STATE_STARTED;
                // 开启的回调
                mCallback.dispatchOnCameraOpened(mCameraOptions);
            }
        });
    }

    // 停止
    public void stop() {

    }

    final void stopImmediately() {
        try {
            // Don't check, try stop again.
            LOG.i("stopImmediately:", "State was:", ss());
            if (mState == STATE_STOPPED) return;
            mState = STATE_STOPPING;
            onStop();
            mState = STATE_STOPPED;
            LOG.i("stopImmediately:", "Stopped. State is:", ss());
        } catch (Exception e) {
            // Do nothing.
            LOG.i("stopImmediately:", "Swallowing exception while stopping.", e);
            mState = STATE_STOPPED;
        }
    }

    @WorkerThread
    protected abstract void onStart();

    // Stops the preview.
    @WorkerThread
    protected abstract void onStop();


    //endregion

    // region -------------------预览
    @Override
    public void onSurfaceAvailable() {

    }

    @Override
    public void onSurfaceChanged() {

    }

    @Override
    public void onSurfaceDestroyed() {

    }

    // region ----------------WorkerHandler回调错误处理
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        // Something went wrong. Thread is terminated (about to?).
        // Move to other thread and release resources.
        if (!(throwable instanceof CameraException)) {
            // This is unexpected, either a bug or something the developer should know.
            // Release and crash the UI thread so we get bug reports.
            LOG.e("uncaughtException:", "Unexpected exception:", throwable);
            destroy();
            mCrashHandler.post(new Runnable() {
                @Override
                public void run() {
                    RuntimeException exception;
                    if (throwable instanceof RuntimeException) {
                        exception = (RuntimeException) throwable;
                    } else {
                        exception = new RuntimeException(throwable);
                    }
                    throw exception;
                }
            });
        } else {
            // At the moment all CameraExceptions are unrecoverable, there was something
            // wrong when starting, stopping, or binding the camera to the preview.
            final CameraException error = (CameraException) throwable;
            LOG.e("uncaughtException:", "Interrupting thread with state:", ss(), "due to CameraException:", error);
            thread.interrupt();
            mHandler = WorkerHandler.get("CameraViewController");
            mHandler.getThread().setUncaughtExceptionHandler(this);
            LOG.i("uncaughtException:", "Calling stopImmediately and notifying.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopImmediately();
                    mCallback.dispatchError(error);
                }
            });
        }
    }

    private static class NoOpExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // No-op.
        }
    }


    //endregion

    public interface Callback {
        void dispatchOnCameraOpened(CameraOptions options);

        void dispatchOnCameraClosed();

        void onCameraPreviewStreamSizeChanged();

        void onShutter(boolean shouldPlaySound);

        //void dispatchOnVideoTaken(VideoResult.Stub stub);

        // void dispatchOnPictureTaken(PictureResult.Stub stub);

        void dispatchOnFocusStart(@Nullable Gesture trigger, @NonNull PointF where);

        void dispatchOnFocusEnd(@Nullable Gesture trigger, boolean success, @NonNull PointF where);

        void dispatchOnZoomChanged(final float newValue, @Nullable final PointF[] fingers);

        void dispatchOnExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers);

        //void dispatchFrame(Frame frame);

        void dispatchError(CameraException exception);
    }

}

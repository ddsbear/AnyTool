package com.dds.videoplayer;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import com.dds.videoplayer.utils.WorkerHandler;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public abstract class MediaInterface  implements TextureView.SurfaceTextureListener {


    public static final String TAG = "IMediaPlayer";
    public static SurfaceTexture SAVED_SURFACE;
    protected WorkerHandler wHandler;
    public VideoViewInterface iVideoView;
    public Handler handler;

    public MediaInterface(VideoViewInterface videoView) {
        this.iVideoView = videoView;
    }

    public abstract void prepare();

    public abstract void start();


    public abstract void pause();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract void setVolume(float left, float right);

    public abstract void setSpeed(float speed);

    public abstract void setSurface(Surface surface);

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract boolean isPlaying();


}

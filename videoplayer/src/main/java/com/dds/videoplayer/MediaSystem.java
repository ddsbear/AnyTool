package com.dds.videoplayer;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.view.Surface;
import android.view.TextureView;
import com.dds.videoplayer.utils.WorkerHandler;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public class MediaSystem extends MediaInterface implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener, TextureView.SurfaceTextureListener {

    private MediaPlayer mediaPlayer;

    public MediaSystem(VideoViewDD videoView) {
        super(videoView);
    }


    @Override
    public void prepare() {
        wHandler = WorkerHandler.get(TAG);
        handler = new Handler();
        wHandler.post(() -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(false);
                // 设置prepare的回调
                mediaPlayer.setOnPreparedListener(MediaSystem.this);
                mediaPlayer.setOnCompletionListener(MediaSystem.this);
                mediaPlayer.setOnBufferingUpdateListener(MediaSystem.this);
                mediaPlayer.setOnSeekCompleteListener(MediaSystem.this);
                mediaPlayer.setOnErrorListener(MediaSystem.this);
                mediaPlayer.setOnInfoListener(MediaSystem.this);
                mediaPlayer.setOnVideoSizeChangedListener(MediaSystem.this);
                // 保持屏幕长亮
                mediaPlayer.setScreenOnWhilePlaying(true);
                // 设置播放路径
                mediaPlayer.setDataSource(iVideoView.dataSource.getCurrentUrl().toString());

                mediaPlayer.setSurface(new Surface(SAVED_SURFACE));
                mediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    @Override
    public void start() {
        wHandler.post(() -> mediaPlayer.start());
    }


    @Override
    public void pause() {
        wHandler.post(() -> mediaPlayer.pause());
    }

    @Override
    public void seekTo(long time) {
        wHandler.post(() -> {
            try {
                mediaPlayer.seekTo((int) time);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void release() {
        WorkerHandler.destroy();
        if (mediaPlayer != null) {
            mediaPlayer = null;
        }

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (wHandler == null) return;
        wHandler.post(() -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(leftVolume, rightVolume);
        });
    }

    @Override
    public void setSpeed(float speed) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams pp = mediaPlayer.getPlaybackParams();
            pp.setSpeed(speed);
            mediaPlayer.setPlaybackParams(pp);
        } else {
            // 23以下設置播放速率
        }

    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    // region  --------各种回调

    // --------------------------setOnPreparedListener-----------------------------
    @Override
    public void onPrepared(MediaPlayer mp) {
        handler.post(() -> iVideoView.onPrepared());
    }

    // -------------------------setOnCompletionListener---------------------------
    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.post(() -> iVideoView.onAutoCompletion());
    }

    // ------------------------setOnBufferingUpdateListener-----------------------
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    // ------------------------setOnSeekCompleteListener-----------------------
    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        wHandler.post(() -> iVideoView.onError(what, extra));
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        handler.post(() -> iVideoView.onInfo(what, extra));
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    //----------------------------------------------------------------------------
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface;
            prepare();
        } else {
            iVideoView.textureView.setSurfaceTexture(SAVED_SURFACE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    // endregion
}

package com.dds.videoplayer;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;
import com.dds.videoplayer.utils.WorkerHandler;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public class MediaSystem extends IMediaPlayer implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {

    public MediaPlayer mediaPlayer;

    public MediaSystem(IVideoView videoView) {
        super(videoView);
    }


    @Override
    public void prepare() {
        wHandler = WorkerHandler.get(TAG);
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
                mediaPlayer.prepareAsync();
                mediaPlayer.setSurface(new Surface(SAVED_SURFACE));

            } catch (Exception e) {
                e.printStackTrace();
            }


        });
    }

    @Override
    public void start() {


    }


    @Override
    public void pause() {

    }

    @Override
    public void seekTo(long time) {

    }

    @Override
    public void release() {

    }

    @Override
    public void setVolume(float left, float right) {

    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public long getCurrentPosition() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    // region  --------各种回调

    // --------------------------setOnPreparedListener-----------------------------
    @Override
    public void onPrepared(MediaPlayer mp) {
        wHandler.post(() -> iVideoView.onPrepared());
    }

    // -------------------------setOnCompletionListener---------------------------
    @Override
    public void onCompletion(MediaPlayer mp) {

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
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    //-----------------------------------------------------------------------------------
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

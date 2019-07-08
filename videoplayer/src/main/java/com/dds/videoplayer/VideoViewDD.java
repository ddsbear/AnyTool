package com.dds.videoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.dds.videoplayer.utils.Utils;

import java.lang.reflect.Constructor;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public class VideoViewDD extends FrameLayout
        implements View.OnClickListener, View.OnTouchListener {

    public static final String TAG = "dds_IVideoView";

    public ImageView startButton;
    public ViewGroup textureViewContainer;
    public TextureViewD textureView;
    public ProgressBar loadingProgressBar;
    public ImageView thumb;


    public MediaInterface iMediaInterface;
    public DataSource dataSource;
    public Class mediaInterfaceClass;

    protected int mScreenWidth;
    protected int mScreenHeight;
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;

    protected AudioManager mAudioManager;

    // 播放状态
    public int state = -1;
    public static final int STATE_IDLE = -1;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARING_CHANGING_URL = 2;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PAUSE = 5;
    public static final int STATE_AUTO_COMPLETE = 6;
    public static final int STATE_ERROR = 7;


    //窗口状态
    public int screen = -1;
    public static final int SCREEN_NORMAL = 0;
    public static final int SCREEN_FULLSCREEN = 1;
    public static final int SCREEN_TINY = 2;


    public static VideoViewDD CURRENT_VP;

    public int getLayoutId() {
        return R.layout.vp_layout_video;
    }


    public VideoViewDD(Context context) {
        super(context);
        init(context);
    }

    public VideoViewDD(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        startButton = findViewById(R.id.start);
        textureViewContainer = findViewById(R.id.surface_container);
        loadingProgressBar = findViewById(R.id.loading);
        thumb = findViewById(R.id.thumb);

        startButton.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        state = STATE_IDLE;
    }


    // ------------------------------------方法调用-------------------------------------
    // 设置参数
    public void setUp(DataSource jzDataSource, int screen, Class mediaInterfaceClass) {
        this.dataSource = jzDataSource;
        this.screen = screen;
        onStateNormal();
        this.mediaInterfaceClass = mediaInterfaceClass;
    }


    // ------------------------------------设置状态-------------------------------------
    // 设置状态 - normal
    public void onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ");
        state = STATE_NORMAL;
        if (iMediaInterface != null) iMediaInterface.release();

        // 更新ui
        changeUiToNormal();
    }

    // 设置状态 - Preparing
    public void onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARING;

        // 更新ui
        changeUiToPreparing();
    }

    // 设置状态 - Prepared
    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARED;

        //准备完成 开始播放
        iMediaInterface.start();

    }

    // 设置状态 - pause
    public void onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ");
        state = STATE_PAUSE;

        changeUiToPauseShow();
    }

    // 设置状态 - Playing
    public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        state = STATE_PLAYING;

        changeUiToPlayingClear();
    }

    // 设置状态 - Error
    public void onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ");
        state = STATE_ERROR;
    }


    public void onInfo(int what, int extra) {
        Log.d(TAG, "onInfo what - " + what + " extra - " + extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (state == VideoViewDD.STATE_PREPARED
                    || state == VideoViewDD.STATE_PREPARING_CHANGING_URL) {
                //真正的prepared，本质上这是进入playing状态。
                onStatePlaying();
            }
        }

    }


    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            iMediaInterface.release();
        }
    }


    // ------------------------------------点击事件----------------------------------------
    @Override
    public void onClick(View v) {
        if (v == startButton) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            // 播放地址无效
            if (dataSource == null || dataSource.urlsMap.isEmpty() || dataSource.getCurrentUrl() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.vp_no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            // 播放
            if (state == STATE_NORMAL) {
                startVideo();
            }
            // 暂停
            else if (state == STATE_PLAYING) {
                Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ");
                iMediaInterface.pause();
                onStatePause();
            }
            // 重新播放
            else if (state == STATE_PAUSE) {
                iMediaInterface.start();
                onStatePlaying();
            }
            // 結束之後重新播放
            else if (state == STATE_AUTO_COMPLETE) {
                startVideo();
            }

        }


    }

    // 开始播放
    public void startVideo() {
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        setCurrentVideoView(this);
        try {
            Constructor<MediaInterface> constructor = mediaInterfaceClass.getConstructor(VideoViewDD.class);
            this.iMediaInterface = constructor.newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // 設置TextureView
        addTextureView();
        // 设置屏幕常亮
        Utils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置状态为Preparing
        onStatePreparing();
    }

    public void onAutoCompletion() {
        Runtime.getRuntime().gc();

    }


    public void reset() {

    }


    public void addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ");
        if (textureView != null) textureViewContainer.removeView(textureView);
        textureView = new TextureViewD(getContext().getApplicationContext());
        textureView.setSurfaceTextureListener(iMediaInterface);

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER);
        textureViewContainer.addView(textureView, layoutParams);
    }


    // --------------------------------更新UI--------------------------------------
    public void changeUiToNormal() {
        updateStartImage();
    }

    public void changeUiToPreparing() {
        // 显示loading
        loadingProgressBar.setVisibility(View.VISIBLE);
        updateStartImage();
    }

    public void changeUiToPlayingShow() {

    }

    public void changeUiToPlayingClear() {
        loadingProgressBar.setVisibility(INVISIBLE);
        thumb.setVisibility(View.GONE);
        updateStartImage();

    }

    public void changeUiToPauseShow() {
        updateStartImage();
    }

    public void changeUiToPauseClear() {

    }

    public void changeUiToComplete() {

    }

    public void changeUiToError() {

    }


    public void updateStartImage() {
        if (state == STATE_PLAYING) {
            // 设置 暂停
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_pause_selector);
        } else if (state == STATE_ERROR) {
            // 隐藏
            startButton.setVisibility(INVISIBLE);
        } else if (state == STATE_AUTO_COMPLETE) {
            // 设置重播
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_replay_selector);
        } else {
            startButton.setImageResource(R.drawable.vp_click_play_selector);
        }
    }


    //--------------------------------onTouch----------------------------

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    //====================================================================
    public static void setCurrentVideoView(VideoViewDD viewDD) {
        if (CURRENT_VP != null) {
            CURRENT_VP.reset();
        }
        CURRENT_VP = viewDD;
    }


    public static void releaseAllVideos() {
        Log.d(TAG, "releaseAllVideos");
        if (CURRENT_VP != null) {
            CURRENT_VP.reset();
            CURRENT_VP = null;
        }
    }


}

package com.dds.videoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.dds.videoplayer.utils.Utils;

import java.lang.reflect.Constructor;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public abstract class VideoViewInterface extends FrameLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, View.OnTouchListener {
    public static final String TAG = "dds_IVideoView";

    public ImageView startButton;
    public SeekBar progressBar;
    public ImageView fullscreenButton;
    public TextView currentTimeTextView, totalTimeTextView;
    public ViewGroup textureViewContainer;
    public ViewGroup topContainer, bottomContainer;
    public TextureViewD textureView;


    public MediaInterface iMediaInterface;
    public DataSource dataSource;
    public Class mediaInterfaceClass;

    protected int mScreenWidth;
    protected int mScreenHeight;
    public static boolean WIFI_TIP_DIALOG_SHOWED = false;

    protected Timer UPDATE_PROGRESS_TIMER;
    protected ProgressTimerTask mProgressTimerTask;

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


    public static VideoViewInterface CURRENT_VP;

    public abstract int getLayoutId();

    protected abstract void showWifiDialog();


    public VideoViewInterface(Context context) {
        super(context);
        init(context);
    }

    public VideoViewInterface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        startButton = findViewById(R.id.start);
        fullscreenButton = findViewById(R.id.fullscreen);
        progressBar = findViewById(R.id.bottom_seek_progress);
        currentTimeTextView = findViewById(R.id.current);
        totalTimeTextView = findViewById(R.id.total);
        bottomContainer = findViewById(R.id.layout_bottom);
        textureViewContainer = findViewById(R.id.surface_container);
        topContainer = findViewById(R.id.layout_top);

        startButton.setOnClickListener(this);
        fullscreenButton.setOnClickListener(this);
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);

        textureViewContainer.setOnTouchListener(this);
        progressBar.setOnSeekBarChangeListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        state = STATE_IDLE;
    }

    protected long goBackFullscreenTime = 0;

    // 设置参数
    public void setUp(DataSource jzDataSource, int screen, Class mediaInterfaceClass) {
        if ((System.currentTimeMillis() - goBackFullscreenTime) < 200) return;
        this.dataSource = jzDataSource;
        this.screen = screen;
        onStateNormal();
        this.mediaInterfaceClass = mediaInterfaceClass;
    }


    // 设置状态 - normal
    public void onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ");
        state = STATE_NORMAL;
        cancelProgressTimer();
        if (iMediaInterface != null) iMediaInterface.release();
    }

    // 设置状态 - Preparing
    public void onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARING;
        resetProgressAndTime();
    }

    // 设置状态 - pause
    public void onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ");
        state = STATE_PAUSE;
        startProgressTimer();
    }

    public void onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ");
        state = STATE_PLAYING;
        startProgressTimer();
    }


    // 设置状态 prepare
    public void onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ");
        state = STATE_PREPARED;

        //这里是 start 哦
        iMediaInterface.start();

    }


    public void onError(int what, int extra) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ");
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            iMediaInterface.release();
        }
    }

    public void onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ");
        state = STATE_ERROR;
        cancelProgressTimer();
    }

    @Override
    public void onClick(View v) {
        if (v == startButton) {
            Log.i(TAG, "onClick start [" + this.hashCode() + "] ");
            // 播放地址无效
            if (dataSource == null || dataSource.urlsMap.isEmpty() || dataSource.getCurrentUrl() == null) {
                Toast.makeText(getContext(), getResources().getString(R.string.no_url), Toast.LENGTH_SHORT).show();
                return;
            }
            // 播放
            if (state == STATE_NORMAL) {
                if (!dataSource.getCurrentUrl().toString().startsWith("file") && !
                        dataSource.getCurrentUrl().toString().startsWith("/") &&
                        !Utils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog();
                    return;
                }
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
            // 重新播放
            else if (state == STATE_AUTO_COMPLETE) {
                startVideo();
            }

        }


    }


    // 开始播放，需要反射获取播放器的类型
    public void startVideo() {
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ");
        setCurrentVideoView(this);
        try {
            Constructor<MediaInterface> constructor = mediaInterfaceClass.getConstructor(VideoViewInterface.class);
            this.iMediaInterface = constructor.newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addTextureView();
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

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

    public void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        currentTimeTextView.setText(Utils.stringForTime(0));
        totalTimeTextView.setText(Utils.stringForTime(0));
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


    public void startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ");
        cancelProgressTimer();
        UPDATE_PROGRESS_TIMER = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }


    //--------------------------------onTouch----------------------------

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    // -------------------------seekBar---------------------------------
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    // ----------------------------------------------------------------
    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (state == STATE_PLAYING || state == STATE_PAUSE) {
                post(() -> {
                    long position = getCurrentPositionWhenPlaying();
                    long duration = getDuration();
                    int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                    onProgress(progress, position, duration);
                });
            }
        }
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        if (state == STATE_PLAYING || state == STATE_PAUSE) {
            try {
                position = iMediaInterface.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        try {
            duration = iMediaInterface.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }


    // 进度显示
    public void onProgress(int progress, long position, long duration) {

    }


    //====================================================================
    public static void setCurrentVideoView(VideoViewInterface jzvd) {
        if (CURRENT_VP != null) {
            CURRENT_VP.reset();
        }
        CURRENT_VP = jzvd;
    }


    //-------------------------------------------------------------------
    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            releaseAllVideos();
                            Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            try {
                                VideoViewInterface player = CURRENT_VP;
                                if (player != null && player.state == VideoViewInterface.STATE_PLAYING) {
                                    player.startButton.performClick();
                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            break;
                    }
                }
            };

    public static void releaseAllVideos() {
        Log.d(TAG, "releaseAllVideos");
        if (CURRENT_VP != null) {
            CURRENT_VP.reset();
            CURRENT_VP = null;
        }
    }


}

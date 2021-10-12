package com.dds.dddemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.dddemo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MediaPlayerActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;

    private List<String> urls = new ArrayList<>();
    private volatile AtomicInteger i = new AtomicInteger(0);
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView text;
    AudioManager audioManager;
    private Thread playWithResetAsyncThread;
    ExecutorService executorService;

    private boolean isBreak = false;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, MediaPlayerActivity.class);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        text = findViewById(R.id.text);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        executorService = Executors.newSingleThreadExecutor();
        mMediaPlayer = new MediaPlayer();
        urls.clear();
        urls.add("http://duer-music.cdn.bcebos.com/audio/128/7878084f35617fbfc91fd9ee7be9ada2.mp3?authorization=bce-auth-v1/d3af5aaafadb4603b04c30e2acb194a6/2021-09-16T11:26:40Z/86400//efc7f8810de2abb5fb15c4001c3711c20c646ca7ac2bfbf56ce5ab15bd7fbe0d");
        urls.add("http://duer-music.cdn.bcebos.com/audio/128/f84f40450f433f8f2ea35ebbb282a7a4.mp3?authorization=bce-auth-v1/d3af5aaafadb4603b04c30e2acb194a6/2021-09-16T11:26:53Z/86400//4545621aa0d68424de31179097fc7d813521f61025d2fce2e383acef1416e4d2");
        urls.add("http://duer-music.cdn.bcebos.com/audio/128/0b24ac402692f20a7996fb34554d131f.mp3?authorization=bce-auth-v1/d3af5aaafadb4603b04c30e2acb194a6/2021-09-16T11:28:04Z/86400//53bd560a670188cdda5afdabd0759a98e4b568c79f1b3cca799343522da3e1d2");
        handler.postDelayed(runnable, 1000);

    }

    private Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            boolean isPlay = false;
            try {
                isPlay = mMediaPlayer != null && mMediaPlayer.isPlaying();
            } catch (Exception e) {
                e.printStackTrace();
            }
            text.setText("播放状态：" +isPlay );
            handler.postDelayed(runnable, 1000);
        }
    };

    public void next(View view) {
        if(isBreak){
            return;
        }
        init();
        if (i.get() > 2 || i.get() < 0) {
            i.set(0);
        }
        String url = urls.get(i.get());
        playWithResetAsyncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("dds_test", url);
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(url);
                    mMediaPlayer.prepareAsync();

                } catch (Exception e) {
                    Log.e("dds_test",e.toString());

                }


                i.addAndGet(1);
            }
        });
//        playWithResetAsyncThread.start();
        executorService.execute(playWithResetAsyncThread);

    }

    public void pre(View view) {
        if(isBreak){
            return;
        }
        init();
        if (i.get() < 0 || i.get() > 2) {
            i.set(2);
        }
        String url = urls.get(i.get());
        playWithResetAsyncThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("dds_test", url);
                mMediaPlayer.reset();
                try {
                    mMediaPlayer.setDataSource(url);
                    mMediaPlayer.prepareAsync();

                } catch (Exception e) {
                    Log.e("dds_test",e.toString());

                }


                i.decrementAndGet();
            }
        });
//        playWithResetAsyncThread.start();
        executorService.execute(playWithResetAsyncThread);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }


    private void init() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
//                    if(mMediaPlayer.isPlaying()){
//                        Log.e("dds_test","isPlaying stop");
//                        stopPlay();
//                        mMediaPlayer = new MediaPlayer();
//                    }
                }else {
                    Log.e("dds_test","isPlaying new mediaplayer");
                    mMediaPlayer =new MediaPlayer();
                }
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e("dds_test", "what:" + what + ",extra:" + extra);
                        isBreak = true;
                        return false;
                    }
                });
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        Log.e("dds_test", "onPrepared");
                        mp.start();
                    }
                });
            }
        });

    }

    public void stopPlay() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (Exception e) {
                e.printStackTrace();
                mMediaPlayer = null;
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    public void stop(View view) {
//        stopPlay();
    }
}
package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.videoplayer.DataSource;
import com.dds.videoplayer.MediaSystem;
import com.dds.videoplayer.VideoViewD;
import com.dds.videoplayer.VideoViewInterface;

import static com.dds.videoplayer.VideoViewInterface.SCREEN_NORMAL;

public class VideoPlayActivity extends AppCompatActivity {
    private VideoViewD videoView;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, VideoPlayActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        initView();
        initData();
    }

    private void initData() {
        DataSource dataSource = null;
        dataSource = new DataSource(Environment.getExternalStorageDirectory() + "/local_video.mp4");
        dataSource.title = "饺子快长大";
        videoView.setUp(dataSource, SCREEN_NORMAL, MediaSystem.class);
    }

    private void initView() {
        videoView = findViewById(R.id.vp_video);
    }


    @Override
    protected void onPause() {
        super.onPause();
        VideoViewInterface.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

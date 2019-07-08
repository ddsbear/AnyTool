package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.videoplayer.DataSource;
import com.dds.videoplayer.MediaSystem;
import com.dds.videoplayer.VideoViewDD;
import com.utils.library.image.ImageLoader;
import com.utils.library.image.glide.GlideILoader;

import static com.dds.videoplayer.VideoViewDD.SCREEN_NORMAL;

public class VideoPlayActivity extends AppCompatActivity {
    private VideoViewDD videoView;

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


    private void initView() {
        videoView = findViewById(R.id.vp_video);
    }

    private void initData() {
        DataSource dataSource = null;
        String thumb = "http://jzvd-pic.nathen.cn/jzvd-pic/1bb2ebbe-140d-4e2e-abd2-9e7e564f71ac.png";
        String url = "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4";
        //String url = Environment.getExternalStorageDirectory() + "/local_video.mp4";
        dataSource = new DataSource(url);
        dataSource.title = "饺子快长大";
        videoView.setUp(dataSource, SCREEN_NORMAL, MediaSystem.class);

        ImageLoader.init(new GlideILoader());

        ImageLoader.getInstance().displayImage(this, thumb, videoView.thumb);
    }


    @Override
    protected void onPause() {
        super.onPause();
        VideoViewDD.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

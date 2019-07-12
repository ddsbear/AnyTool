package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.dblibrary.LitePalUtil;

public class LitePalActivity extends AppCompatActivity {


    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, LitePalActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_litepal);

        // 初始化
        LitePalUtil.init(App.getApp());
    }
}

package com.dds.dddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.common.Toasts;
import com.dds.common.permission.Permissions;
import com.dds.dddemo.ui.DialogActivity;
import com.dds.dddemo.ui.DrawHelperActivity;
import com.dds.dddemo.ui.HackActivity;
import com.dds.dddemo.ui.MediaPlayerActivity;
import com.dds.dddemo.ui.ToastActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // Permission
    public void onPermission(View view) {
        Permissions.request(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, integer -> {
            if (integer == PackageManager.PERMISSION_GRANTED) {
                Toasts.showShort(MainActivity.this, "成功accept : " + integer);
            } else {
                Toasts.showShort(MainActivity.this, "失败accept : " + integer);
            }
        });

    }

    // Dialog
    public void onDialog(View view) {
        DialogActivity.openActivity(this);
    }

    // Toast
    public void onToast(View view) {
        ToastActivity.openActivity(this);


    }

    // hook
    public void onHook(View view) {
        HackActivity.openActivity(this);
    }


    // test JetPack
    public void onJetpack(View view) {
        // JetPackActivity.openActivity(this);

    }


    // drawable
    public void DrawableHelper(View view) {
        DrawHelperActivity.openActivity(this);


    }


    public void onTest(View view) {
        MediaPlayerActivity.openActivity(this);
    }

}

package com.utils.dddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.utils.library.Permissions;
import com.utils.library.Toasts;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // Permission
    public void onPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Permissions.request(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, integer -> {
                if (integer == PackageManager.PERMISSION_GRANTED) {
                    Toasts.showShort(MainActivity.this, "成功accept : " + integer);
                } else {
                    Toasts.showShort(MainActivity.this, "失败accept : " + integer);
                }
            });
        }

    }

    // Dialog
    public void onDialog(View view) {
        DialogActivity.openActivity(this);
    }

    // hook
    public void onHook(View view) {
        HackActivity.openActivity(this);
    }

    // Toast
    public void onToast(View view) {
        ToastActivity.openActivity(this);


    }

}

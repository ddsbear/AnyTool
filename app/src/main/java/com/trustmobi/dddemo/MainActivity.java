package com.trustmobi.dddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.trustmobi.library.Dialogs;
import com.trustmobi.library.Permissions;
import com.trustmobi.library.utils.VToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    // Permission
    public void onPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Permissions.request(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, integer -> {
                if (integer == PackageManager.PERMISSION_GRANTED) {
                    VToast.show(MainActivity.this, "成功accept : " + integer);
                } else {
                    VToast.show(MainActivity.this, "失败accept : " + integer);
                }


            });
        }

    }

    // Dialog
    public void onDialog(View view) {
        Dialogs.buildAlert(this, "title", "content ")
                .withOkButton(() -> {
                    VToast.show(MainActivity.this, "onDialog");
                })
                .withCancelButton()
                .show();

    }

    // ProgressDialog
    public void onProgressDialog(View view) {
        Dialogs.FluentProgressDialog fluentProgressDialog = Dialogs.buildProgress(this, "转转。。。");
        fluentProgressDialog
                .indeterminate()
                .nonCancelable()
                .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fluentProgressDialog.dismiss();
                    }
                });

            }
        }, 2000);

    }
}

package com.trustmobi.dddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.trustmobi.library.Dialogs;
import com.trustmobi.library.Permissions;
import com.trustmobi.library.SnackBars;
import com.trustmobi.library.Toasts;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onTest(View view) {
        SnackBars.Additional ad = SnackBars.withLink("百度", Uri.parse("https://www.baidu.com"));
        Snackbar snackbar = SnackBars.make(view, "dds", ad);
        if (snackbar != null) {
            snackbar.show();
        }
//        Snackbar.make(view, "dds", Snackbar.LENGTH_SHORT)
//                .setAction("dds", null)
//                .show();

    }

    // Permission
    public void onPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Permissions.request(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, integer -> {
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
        Dialogs.buildAlert(this, "title", "content ")
                .withOkButton(() -> {
                    Toasts.showShort(MainActivity.this, "onDialog");
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


    // hook
    public void onHook(View view) {
        HackActivity.openActivity(this);
    }
}

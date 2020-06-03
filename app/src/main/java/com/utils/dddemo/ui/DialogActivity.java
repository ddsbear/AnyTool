package com.utils.dddemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.utils.dddemo.R;
import com.utils.library.Dialogs;
import com.utils.library.Toasts;

public class DialogActivity extends AppCompatActivity {
    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, DialogActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
    }

    public void onDialog(View view) {
        Dialogs.buildAlert(this, "title", "content ")
                .withOkButton(() -> {
                    Toasts.showShort(DialogActivity.this, "onDialog");
                })
                .withCancelButton()
                .show();
    }

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

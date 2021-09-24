package com.dds.dddemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.dds.dddemo.R;
import com.dds.http.snack.SnackBars;
import com.dds.http.Toasts;

public class ToastActivity extends AppCompatActivity {

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, ToastActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);
    }

    public void onSnake(View view) {
        SnackBars.Additional ad = SnackBars.withLink("百度", Uri.parse("https://www.baidu.com"));
        Snackbar snackbar = SnackBars.make(view, "dds", ad);
        if (snackbar != null) {
            snackbar.show();
        }

    }

    public void onToast(View view) {
        Toasts.showLong(this, "Toast......");
    }
}

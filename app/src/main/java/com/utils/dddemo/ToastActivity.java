package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.utils.library.SnackBars;
import com.utils.library.Toasts;

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

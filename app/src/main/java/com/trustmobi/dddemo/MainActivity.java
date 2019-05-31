package com.trustmobi.dddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.trustmobi.library.Permissions;
import com.trustmobi.library.utils.Consumer;
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
            Permissions.request(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) {
                    if (integer == PackageManager.PERMISSION_GRANTED) {
                        VToast.show(MainActivity.this, "成功accept : " + integer);
                    } else {
                        VToast.show(MainActivity.this, "失败accept : " + integer);
                    }


                }
            });
        }

    }
}

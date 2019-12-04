package com.dds.jetpack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.jetpack.room.AppDataBase;
import com.dds.jetpack.room.entity.UserInfo;

import java.util.List;
import java.util.Random;

public class RoomActivity extends AppCompatActivity {
    private TextView text;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, RoomActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        text = findViewById(R.id.text);
        new Thread(new Runnable() {
            @Override
            public void run() {
                onSearch();
            }
        }).start();
    }

    public void onAdd(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(new Random().nextInt(10000));
                userInfo.setUserName("大大帅");
                userInfo.setNickName("屠~~");
                userInfo.setAvatar("jpeg");
                userInfo.setEmail("android_shuai@163.com");
                AppDataBase.getAppDataBase(RoomActivity.this).getUserDao().insert(userInfo);
                onSearch();
            }
        }).start();


    }

    public void onDelete(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(4934);
                AppDataBase.getAppDataBase(RoomActivity.this).getUserDao().delete(userInfo);
                onSearch();
            }
        }).start();
    }

    public void onUpdate(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInfo userInfo = AppDataBase.getAppDataBase(RoomActivity.this).getUserDao().getUser();
                userInfo.setUserName("小小帥");
                AppDataBase.getAppDataBase(RoomActivity.this).getUserDao().update(userInfo);
                onSearch();
            }
        }).start();
    }

    public void onSearch() {
        List<UserInfo> all = AppDataBase.getAppDataBase(this).getUserDao().getAll();
        StringBuilder builder = new StringBuilder();
        for (UserInfo userInfo : all) {
            builder.append(userInfo.toString());
            builder.append("\n");
        }

        final String str = builder.toString();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(str);
            }
        });

    }


}

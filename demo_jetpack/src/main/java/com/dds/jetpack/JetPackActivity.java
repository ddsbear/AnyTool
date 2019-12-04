package com.dds.jetpack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class JetPackActivity extends AppCompatActivity {

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, JetPackActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jet_pack);
    }

    public void onRoom(View view) {
        RoomActivity.openActivity(this);


    }

    public void onCamera(View view) {
        CameraXActivity.openActivity(this);

    }
}

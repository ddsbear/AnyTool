package com.dds.uilibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class UILibraryActivity extends AppCompatActivity {


    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageView2;


    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, UILibraryActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uilibrary);
        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);

        Glide.with(this).load(R.drawable.test).into(imageView);
        Glide.with(this).load(R.drawable.test1).into(imageView1);
        Glide.with(this).load(R.drawable.test2).into(imageView2);
    }

    public void onDragBack(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        DragPhotoActivity.openActivity(this, location[0], location[1], view.getWidth(), view.getHeight());

    }
}

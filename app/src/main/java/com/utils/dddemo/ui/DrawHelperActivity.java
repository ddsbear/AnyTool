package com.utils.dddemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.utils.dddemo.R;
import com.utils.library.helper.DrawableHelper;

public class DrawHelperActivity extends AppCompatActivity {

    private ImageView image;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, DrawHelperActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_helper);
        image = findViewById(R.id.image);
    }

    public void onDisplay(View view) {

        // 创建一个纯色图片
//        BitmapDrawable drawableWithSize = DrawableHelper.createDrawableWithSize(getResources(), 200, 300, 10, getColor(R.color.colorPrimary));
//        image.setImageDrawable(drawableWithSize);

        // 创建一个渐变图片
//        GradientDrawable circleGradientDrawable = DrawableHelper.createCircleGradientDrawable(
//                ContextCompat.getColor(this, R.color.colorAccent),
//                ContextCompat.getColor(this, R.color.colorPrimaryDark),
//                10, 1f, 1f);
//        image.setImageDrawable(circleGradientDrawable);
        LayerDrawable itemSeparatorBg = DrawableHelper.createItemSeparatorBg(ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                10, false);
        image.setBackground(itemSeparatorBg);
    }
}

package com.dds.common.image;

import android.app.Activity;
import android.widget.ImageView;


public interface ILoader {


    void displayImage(Activity activity, String imageUrl, ImageView imageView);

}

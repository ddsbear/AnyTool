package com.utils.library.image.glide;

import android.app.Activity;
import android.widget.ImageView;
import com.utils.library.image.ImageLoader;


public class GlideImageLoader implements ImageLoader {

    public GlideImageLoader() {
    }

    @Override
    public void displayImage(Activity activity, String imageUrl, ImageView view) {
        // Glide.with(activity).load(imageUrl).into(view);
    }
}

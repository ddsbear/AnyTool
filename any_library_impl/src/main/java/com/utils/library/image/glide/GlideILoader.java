package com.utils.library.image.glide;

import android.app.Activity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.utils.library.image.ILoader;


public class GlideILoader implements ILoader {

    public GlideILoader() {
    }

    @Override
    public void displayImage(Activity activity, String imageUrl, ImageView view) {
        Glide.with(activity).load(imageUrl).into(view);
    }
}

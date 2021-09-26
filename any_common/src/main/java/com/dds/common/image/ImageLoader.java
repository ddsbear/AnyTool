package com.dds.common.image;

import android.app.Activity;
import android.widget.ImageView;


public class ImageLoader implements ILoader {

    private ILoader imageLoader;
    private static ImageLoader instance;

    public static ImageLoader getInstance() {
        return instance;
    }

    private ImageLoader(ILoader loader) {
        imageLoader = loader;
    }


    public static void init(ILoader loader) {
        if (null == instance) {
            synchronized (ImageLoader.class) {
                if (null == instance) {
                    instance = new ImageLoader(loader);
                }
            }
        }

    }

    @Override
    public void displayImage(Activity activity, String imageUrl, ImageView imageView) {
        imageLoader.displayImage(activity, imageUrl, imageView);
    }
}

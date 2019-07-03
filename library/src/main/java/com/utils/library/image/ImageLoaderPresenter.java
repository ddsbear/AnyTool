package com.utils.library.image;

import android.app.Activity;
import android.widget.ImageView;



public class ImageLoaderPresenter implements ImageLoader {

    private ImageLoader imageLoader;
    private static ImageLoaderPresenter instance;

    public static ImageLoaderPresenter getInstance() {
        return instance;
    }

    private ImageLoaderPresenter(ImageLoader loader) {
        imageLoader = loader;
    }


    public static void init(ImageLoader loader) {
        if (null == instance) {
            synchronized (ImageLoaderPresenter.class) {
                if (null == instance) {
                    instance = new ImageLoaderPresenter(loader);
                }
            }
        }

    }

    @Override
    public void displayImage(Activity activity, String imageUrl, ImageView imageView) {
        imageLoader.displayImage(activity, imageUrl, imageView);
    }
}

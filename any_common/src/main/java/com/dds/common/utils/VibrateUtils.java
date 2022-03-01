package com.dds.common.utils;


import static android.Manifest.permission.VIBRATE;

import android.content.Context;
import android.os.Vibrator;

import androidx.annotation.RequiresPermission;

public class VibrateUtils {

    private static Vibrator vibrator;

    private VibrateUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param milliseconds The number of milliseconds to vibrate.
     */
    @RequiresPermission(VIBRATE)
    public static void vibrate(Context context,final long milliseconds) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null) return;
        vibrator.vibrate(milliseconds);
    }

    /**
     * Vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param pattern An array of longs of times for which to turn the vibrator on or off.
     * @param repeat  The index into pattern at which to repeat, or -1 if you don't want to repeat.
     */
    @RequiresPermission(VIBRATE)
    public static void vibrate(Context context,final long[] pattern, final int repeat) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null) return;
        vibrator.vibrate(pattern, repeat);
    }

    /**
     * Cancel vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     */
    @RequiresPermission(VIBRATE)
    public static void cancel(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null) return;
        vibrator.cancel();
    }

    private static Vibrator getVibrator(Context context) {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return vibrator;
    }
}

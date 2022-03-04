package com.dds.common.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;


public final class Utils {

    private static Application sApp;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Init utils.
     * <p>Init it in the class of UtilsFileProvider.</p>
     *
     * @param app application
     */
    public static void init(final Application app) {
        if (app == null) {
            Log.e("Utils", "app is null.");
            return;
        }
        if (sApp == null) {
            sApp = app;
            UtilsBridge.init(sApp);
            return;
        }
        if (sApp.equals(app)) return;
        UtilsBridge.unInit(sApp);
        sApp = app;
        UtilsBridge.init(sApp);
    }

    /**
     * Register the status of application changed listener.
     *
     * @param listener The status of application changed listener
     */
    public static void registerAppStatusChangedListener(@NonNull final Utils.OnAppStatusChangedListener listener) {
        UtilsBridge.addOnAppStatusChangedListener(listener);
    }

    /**
     * Unregister the status of application changed listener.
     *
     * @param listener The status of application changed listener
     */
    public static void unregisterAppStatusChangedListener(@NonNull final Utils.OnAppStatusChangedListener listener) {
        UtilsBridge.removeOnAppStatusChangedListener(listener);
    }

    /**
     * app是否处于前台
     * @return 是否在前台
     */
    public static boolean isAppForeground() {
        return UtilsBridge.isAppForeground();
    }

    /**
     * Return the Application object.
     * <p>Main process get app by UtilsFileProvider,
     * and other process get app by reflect.</p>
     *
     * @return the Application object
     */
    public static Application getApp() {
        if (sApp != null) return sApp;
        init(UtilsBridge.getApplicationByReflect());
        if (sApp == null) throw new NullPointerException("reflect failed.");
        Log.i("Utils", UtilsBridge.getCurrentProcessName() + " reflect app success.");
        return sApp;
    }

    public interface OnAppStatusChangedListener {
        void onForeground(Activity activity);

        void onBackground(Activity activity);
    }

    public static class ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity) {/**/}

        public void onActivityStarted(Activity activity) {/**/}

        public void onActivityResumed(Activity activity) {/**/}

        public void onActivityPaused(Activity activity) {/**/}

        public void onActivityStopped(Activity activity) {/**/}

        public void onActivityDestroyed(Activity activity) {/**/}

        public void onLifecycleChanged(Activity activity, Lifecycle.Event event) {/**/}
    }

}
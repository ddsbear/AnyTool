package com.utils.library;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.utils.library.utils.Consumer;

import static android.os.Build.VERSION_CODES.M;

/**
 * Permission-related helpers
 */
public class Permissions {


    private static boolean has(final Context context, final String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param callback will be called if request is not canceled, with either
     *                 {@link PackageManager#PERMISSION_GRANTED} or {@link PackageManager#PERMISSION_DENIED}
     */
    @RequiresApi(M)
    public static void request(final Activity activity, final String permission, final Consumer<Integer> callback) {
        final FragmentManager fm = activity.getFragmentManager();
        if (!has(activity, permission)) {
            fm.beginTransaction().add(new PermissionRequestFragment(permission, callback), null).commitAllowingStateLoss();
        } else {
            callback.accept(PackageManager.PERMISSION_GRANTED);
        }
    }

    @RequiresApi(M)
    public static class PermissionRequestFragment extends Fragment {

        @SuppressLint("ValidFragment")
        public PermissionRequestFragment(@NonNull final String permission, @NonNull final Consumer<Integer> callback) {
            mPermission = permission;
            mCallback = callback;
        }

        @Override
        public void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (mPermission != null) requestPermissions(new String[]{mPermission}, 0);
        }

        @Override
        public void onRequestPermissionsResult(final int request, @NonNull final String[] permissions, @NonNull final int[] results) {
            getFragmentManager().beginTransaction().remove(this).commit();
            if (mCallback == null || results.length == 0/* canceled */) return;
            mCallback.accept(results[0]);
        }

        public PermissionRequestFragment() {
            mPermission = null;
            mCallback = null;
        }

        private final @Nullable
        String mPermission;
        private final @Nullable
        Consumer<Integer> mCallback;
    }
}

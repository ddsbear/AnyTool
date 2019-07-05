package com.dds.videoplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

/**
 * Created by dds on 2019/7/4.
 * android_shuai@163.com
 */
public class VideoViewD extends VideoViewInterface {

    public VideoViewD(Context context) {
        super(context);
    }

    public VideoViewD(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.vp_layout_video;
    }

    @Override
    protected void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(getResources().getString(R.string.tips_not_wifi_confirm), (dialog, which) -> {
            dialog.dismiss();
            startVideo();
            WIFI_TIP_DIALOG_SHOWED = true;
        });
        builder.setNegativeButton(getResources().getString(R.string.tips_not_wifi_cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setOnCancelListener(DialogInterface::dismiss);
        builder.create().show();
    }
}

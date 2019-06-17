package com.dds.jetpack;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by dds on 2019/6/16.
 * android_shuai@163.com
 */

public class UploadWorker extends Worker {


    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        uploadImages();
        return Result.success();
    }

    private void uploadImages() {

    }
}

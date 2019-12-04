package com.dds.jetpack.workmanager;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.UUID;

/**
 * Created by dds on 2019/6/16.
 * android_shuai@163.com
 */
public class Utils {

    public static void enqueue(AppCompatActivity activity) {
        Constraints myConstraints = new Constraints.Builder()
//                .setRequiresDeviceIdle(true)
//                .setRequiresCharging(true)
//                .setRequiresBatteryNotLow()
//                .setRequiredNetworkType()
//                .setRequiresStorageNotLow()
//                .setTriggerContentUpdateDelay()
                .build();

        OneTimeWorkRequest compressionWork =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setConstraints(myConstraints)
//                        .setInitialDelay()
//                        .setInputMerger()
//                        .addTag()
                        .build();
        WorkManager.getInstance().enqueue(compressionWork);


        // 取消任务
        UUID id = compressionWork.getId();
        WorkManager.getInstance().cancelWorkById(id);

        //
        LiveData<WorkInfo> status = WorkManager.getInstance().getWorkInfoByIdLiveData(compressionWork.getId());
        status.observe(activity, new Observer<WorkInfo>() {
            @Override
            public void onChanged(@Nullable WorkInfo workInfo) {

            }
        });


    }

    public static boolean isFinish() {
        return false;
    }

}

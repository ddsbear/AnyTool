package com.dds.dblibrary;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by dds on 2019/9/23.
 * android_shuai@163.com
 */
public enum DBManager {
    INSTANCE;
    private Lock mLock;

    DBManager() {
        mLock = new ReentrantLock();
    }

}

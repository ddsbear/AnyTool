package com.dds.dbframwork.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

/**
 * Created by dds on 2019/7/12.
 * android_shuai@163.com
 */
public class DaoFactory {
    private final static String TAG = "dds_DaoFactory";
    private static final DaoFactory factory = new DaoFactory();

    public static DaoFactory getInstance() {
        return factory;
    }

    // dataBase实例
    private SQLiteDatabase sdb;
    // 数据库名称
    protected String mDbName;
    // 数据库路径
    protected String mDbPath;

    protected DaoFactory() {
    }

    public void init(Context context, String dbName) {
        if (null == sdb) {
            mDbName = dbName;
            mDbPath = context.getDatabasePath(dbName).getPath();
            File dir = new File(context.getDatabasePath(dbName).getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            sdb = SQLiteDatabase.openOrCreateDatabase(mDbPath, null);
        }
    }

    public synchronized <T> BaseDao getBaseDao(Class<T> entityClass) {
        BaseDao<T> baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sdb, entityClass);
        } catch (Exception e) {
            Log.i(TAG, "getBaseDao failed:" + e.toString());
        }
        return baseDao;
    }
}

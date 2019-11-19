package com.dds.dbframwork.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    protected SQLiteDatabase sdb;
    // 数据库名称
    protected String mDbName;
    // 数据库路径
    protected String mDbPath;
    // 数据库父路径
    protected String mDbParentDir;

    protected Map<String, BaseDao> map = Collections.synchronizedMap(new HashMap<String, BaseDao>());

    protected DaoFactory() {
    }

    public void init(Context context, String dbName) {
        if (null == sdb) {
            mDbName = dbName;
            mDbPath = context.getDatabasePath(dbName).getPath();
            mDbParentDir = context.getDatabasePath(dbName).getParent();

            File dir = new File(mDbParentDir);
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

    // 根据dao和对象获取BaseDao对象
    public synchronized <T extends BaseDao<M>, M> T getBaseDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(daoClass.getSimpleName());
        }
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(sdb, entityClass);
            map.put(daoClass.getSimpleName(), baseDao);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;
    }
}

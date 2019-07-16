package com.dds.dbframwork.sub_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.dds.dbframwork.db.BaseDao;
import com.dds.dbframwork.db.DaoFactory;

import java.io.File;

/**
 * Created by dds on 2019/7/16.
 * android_shuai@163.com
 */
public class SubDaoFactory extends DaoFactory {
    private final static String TAG = "dds_SubDaoFactory";

    private SubDaoFactory() {
    }

    private static final SubDaoFactory ourInstance = new SubDaoFactory();

    public static SubDaoFactory getInstance() {
        return ourInstance;
    }

    //用于实现分库
    private SQLiteDatabase subSdb;

    @Override
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

    // 获取子查询Dao
    public synchronized <T extends BaseDao<M>, M> T getSubDao(Class<T> daoClass, Class<M> entityClass) {
        BaseDao baseDao = null;
        if (map.get(daoClass.getSimpleName()) != null) {
            return (T) map.get(DbEnums.database.getValue(mDbParentDir));
        }
        String dbPath = DbEnums.database.getValue(mDbParentDir);
        if (TextUtils.isEmpty(dbPath)) {
            return null;
        }
        Log.i(TAG, "sub db path:" + dbPath);
        subSdb = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
        try {
            baseDao = daoClass.newInstance();
            baseDao.init(subSdb, entityClass);
            map.put(daoClass.getSimpleName(), baseDao);
            return (T) baseDao;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}

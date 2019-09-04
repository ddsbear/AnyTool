package com.dds.dbframwork.sub_db;

import android.util.Log;
import com.dds.dbframwork.db.BaseDao;

import java.util.List;

/**
 * Created by dds on 2019/7/15.
 * android_shuai@163.com
 */
public class UserDao extends BaseDao<User> {
    private final static String TAG = "dds_UserDao";

    enum State {Default, Login}

    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where;
        for (User user : list) {
            if (entity.getId().equals(user.getId())) {
                continue;
            }

            if (user.getState() == State.Default.ordinal()) {
                continue;
            }
            // 设置之前用户为未登录
            where = new User();
            where.setId(user.getId());
            user.setState(State.Default.ordinal());
            update(user, where);
            Log.i(TAG, user.getName() + "-->logout");
        }
        // 设置当前用户为登录状态
        entity.setState(State.Login.ordinal());
        where = new User();
        where.setId(entity.getId());
        int result = update(entity, where);
        if (result > 0) {
            Log.i(TAG, entity.getName() + "-->reLogin");
            return result;
        } else {
            Log.i(TAG, entity.getName() + "-->first login");
            return super.insert(entity);
        }


    }


    // 获取当前登录的用户信息
    public User getCurrentUser() {
        User user = new User();
        user.setState(State.Login.ordinal());
        List<User> list = query(user);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}

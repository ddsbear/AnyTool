package com.dds.dbframwork.sub_db;

import com.dds.dbframwork.db.BaseDao;

import java.util.List;

/**
 * Created by dds on 2019/7/15.
 * android_shuai@163.com
 */
public class UserDao extends BaseDao<User> {


    enum State {Default, Login}

    @Override
    public long insert(User entity) {
        List<User> list = query(new User());
        User where;
        for (User user : list) {
            // 设置其他人为未登录
            where = new User();
            where.setId(user.getId());
            user.setState(State.Default.ordinal());
            update(user, where);
        }
        // 设置当前用户为登录状态
        entity.setState(State.Login.ordinal());
        return super.insert(entity);
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

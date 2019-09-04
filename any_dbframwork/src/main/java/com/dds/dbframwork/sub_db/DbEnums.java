package com.dds.dbframwork.sub_db;

import com.dds.dbframwork.db.DaoFactory;

import java.io.File;


public enum DbEnums {

    database("");

    private String value;

    DbEnums(String value) {
        this.value = value;
    }

    public String getValue(String parentDir) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File(parentDir, currentUser.getId());
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/mixin.db";
            }

        }
        return value;
    }


}

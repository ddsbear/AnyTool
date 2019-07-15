package com.utils.dddemo.db;


import com.dds.dbframwork.annotation.DbField;
import com.dds.dbframwork.annotation.DbTable;


@DbTable("tb_user")
public class User {
    @DbField("_id")
    private String id;
    private String name;
    private String password;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }


    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

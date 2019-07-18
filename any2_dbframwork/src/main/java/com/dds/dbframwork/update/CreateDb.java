package com.dds.dbframwork.update;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dds on 2019/7/17.
 * android_shuai@163.com
 */
public class CreateDb {

    // 表名
    private String tableName;


    private List<String> sqlCreates;


    public CreateDb(Element ci) {
        tableName = ci.getAttribute("name");
        sqlCreates = new ArrayList<>();
        NodeList sqls = ci.getElementsByTagName("sql_createTable");
        for (int i = 0; i < sqls.getLength(); i++) {
            String sqlCreate = sqls.item(i).getTextContent();
            this.sqlCreates.add(sqlCreate);
        }


    }


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getSqlCreates() {
        return sqlCreates;
    }

    public void setSqlCreates(List<String> sqlCreates) {
        this.sqlCreates = sqlCreates;
    }
}

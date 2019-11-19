package com.dds.dbframwork.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by dds on 2019/7/12.
 * android_shuai@163.com
 */
public class Condition {
    public String whereCause;//"name=? && password=?...."
    public String[] whereArgs;//new String[]{"张三"}

    public Condition(Map<String, String> whereCause) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1=1 ");
        Set keys = whereCause.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = whereCause.get(key);
            if (value != null) {
                stringBuilder.append(" and " + key + "=?");
                list.add(value);
            }
        }
        this.whereCause = stringBuilder.toString();
        this.whereArgs = (String[]) list.toArray(new String[list.size()]);
    }
}

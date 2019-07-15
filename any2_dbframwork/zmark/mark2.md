这是手撸数据库框架的第二篇

之前完成了一篇文章

[Android徒手撸数据库系列——注解与反射数据库关系模型](https://blog.csdn.net/u011077027/article/details/95646227)

下面继续上一篇没有完成的内容

# 目录

[TOC]

## 1. 数据的更新

数据的更新其实就是比数据的插入多了条件的查询

我们使用SQLiteDatabase中这个方法进行更新数据库

```java
   /**
     * Convenience method for updating rows in the database.
     *
     * @param table the table to update in
     * @param values a map from column names to new column values. null is a
     *            valid value that will be translated to NULL.
     * @param whereClause the optional WHERE clause to apply when updating.
     *            Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which
     *            will be replaced by the values from whereArgs. The values
     *            will be bound as Strings.
     * @return the number of rows affected
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return updateWithOnConflict(table, values, whereClause, whereArgs, CONFLICT_NONE);
    }

```

我们可以看到多了参数whereClause和whereArgs

获取需要更新的字段与插入时相同

```java
   Map<String, String> values = getValues(entity);
   ContentValues contentValues = getContentValues(values);
```

然后创建表示条件的对象

```java
public class Condition {
    public String whereCause;//"name=? && password=?...."
    public String[] whereArgs;//new String[]{"ddssingsong"}

    // 构造查询语句
    public Condition(Map<String, String> whereCause) {
        ArrayList list = new ArrayList();
        StringBuilder stringBuilder = new StringBuilder();
        // 构造时忽略第一个and语句
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
```

整个方法如下

```java
 @Override
    public int update(T entity, T where) {
        Map<String, String> values = getValues(entity);
        ContentValues contentValues = getContentValues(values);
        Map<String, String> whereCause = getValues(where);
        Condition condition = new Condition(whereCause);
        return mSqLiteDatabase.update(mTableName, contentValues, condition.whereCause, condition.whereArgs);
    }

```



## 2. 数据的删除

数据库删除和更新差不多

```java
  @Override
    public int delete(T where) {
        Map<String, String> map = getValues(where);
        Condition condition = new Condition(map);
        return mSqLiteDatabase.delete(mTableName, condition.whereCause, condition.whereArgs);}
```



## 3. 数据的查询

查询的话拿到Cursor后需要对返回结果进行处理

```java
  private List<T> getResult(Cursor cursor, T where) {
        ArrayList<T> list = new ArrayList();
        T item;
        while (cursor.moveToNext()) {
            try {
                item = (T) where.getClass().newInstance();
                for (Map.Entry<String, Field> stringFieldEntry : cacheMap.entrySet()) {
                    // 获取列名
                    String columnName = (String) ((Map.Entry) stringFieldEntry).getKey();
                    // 根据列名拿到游标的位置
                    int columnIndex = cursor.getColumnIndex(columnName);
                    Field field = (Field) ((Map.Entry) stringFieldEntry).getValue();
                    Class type = field.getType();
                    if (columnIndex != -1) {
                        if (type == String.class) {
                            //反射方式赋值
                            field.set(item, cursor.getString(columnIndex));
                        } else if (type == Double.class) {
                            field.set(item, cursor.getDouble(columnIndex));
                        } else if (type == Integer.class) {
                            field.set(item, cursor.getInt(columnIndex));
                        } else if (type == Long.class) {
                            field.set(item, cursor.getLong(columnIndex));
                        } else if (type == byte[].class) {
                            field.set(item, cursor.getBlob(columnIndex));
                            /*
                            不支持的类型
                             */
                        } else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return list;
    }
```

从之前缓存的字段信息中查询出对应的字段信息，然后使用反射对其赋值

下面是整个查询方法

```java
   @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map map = getValues(where);
        String limitString = null;
        if (startIndex != null && limit != null) {
            limitString = startIndex + " , " + limit;
        }
        Condition condition = new Condition(map);
        Cursor cursor = mSqLiteDatabase.query(mTableName, null, condition.whereCause
                , condition.whereArgs, null, null, orderBy, limitString);
        List<T> result = getResult(cursor, where);
        cursor.close();
        return result;

    }
```



## 详细代码

https://github.com/ddssingsong/AnyTool




## 前言

我们看看现在市面上有很多的数据库框架

[LitePal](https://github.com/LitePalFramework/LitePal)

[GreenDao](https://github.com/greenrobot/greenDAO)

OrmLite

Realm

DBFlow

他们各有各的优势，各有各的缺点，不管怎样，都是为了让我们使用数据库简单一些

好了，进入正题

咱们来自己撸一个orm数据库框架，就让我的这篇博客作为这个系列的开篇，让我们一起见证他的诞生

**本篇目录**

[TOC]

## 1. 设计增删改查的接口

先设计简单的增删改查

```java
public interface IDao<T> {
    // 插入
    long insert(T entity);
    // 更新
    int update(T entity, T where);
    // 删除
    int delete(T where);
    // 查询
    List<T> query(T where);
}
```

## 2. 完成建表

建表该如何建呢？

我们知道创建表需要知道表名和字段信息，我们如何拿到表名和字段信息呢？

如何像使用greenDao或者其他数据那样根据实体类自动创建数据库表呢？



参考greenDao的做法，通过反射和注解的方式获取到类名

反射可以通过`class.getSimpleName`获取到实体类的类名

注解可以获取到Class实体类设置的表名

表名注解如下

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbTable {
    String value();
}
```

同样的套路：字段名注解

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbField {
    String value();
}
```



实体类如下配置

```java
@DbTable("tb_user")    // 设置表名
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
}

```

那么，整个创建表的过程如下

```java
    private SQLiteDatabase mSqLiteDatabase;
    private String mTableName;
    private Class<T> mEntityClass;
    // 是否创建成功
    private boolean isInit = false;

    private HashMap<String, Field> cacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        mSqLiteDatabase = sqLiteDatabase;
        mEntityClass = entityClass;
        if (!isInit) {
            //取到表名
            if (entityClass.getAnnotation(DbTable.class) == null) {
                //反射到类名
                mTableName = entityClass.getSimpleName();
            } else {
                //取注解上的名字
                mTableName = entityClass.getAnnotation(DbTable.class).value();
            }
        }
        if (!sqLiteDatabase.isOpen()) {
            Log.e(TAG, "SQLiteDatabase is not open!");
            return false;
        }
        //执行建表
        String sql = generateCreateTableSql();
        Log.i(TAG, sql);
        sqLiteDatabase.execSQL(sql);
        isInit = true;
        return false;
    }

    // 生成创建数据表语句
    private String generateCreateTableSql() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists ");
        sb.append(mTableName).append("(");
        //反射得到所有的成员变量
        Field[] fields = mEntityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if (field.getAnnotation(DbField.class) != null) {
                if (type == String.class) {
                    sb.append(field.getAnnotation(DbField.class).value()).append(" TEXT,");
                } else if (type == Integer.class) {
                    sb.append(field.getAnnotation(DbField.class).value()).append(" INTEGER,");
                } else if (type == Long.class) {
                    sb.append(field.getAnnotation(DbField.class).value()).append(" BIGINT,");
                } else if (type == Double.class) {
                    sb.append(field.getAnnotation(DbField.class).value()).append(" DOUBLE,");
                } else if (type == byte[].class) {
                    sb.append(field.getAnnotation(DbField.class).value()).append(" BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            } else {
                if (type == String.class) {
                    sb.append(field.getName()).append(" TEXT,");
                } else if (type == Integer.class) {
                    sb.append(field.getName()).append(" INTEGER,");
                } else if (type == Long.class) {
                    sb.append(field.getName()).append(" BIGINT,");
                } else if (type == Double.class) {
                    sb.append(field.getName()).append(" DOUBLE,");
                } else if (type == byte[].class) {
                    sb.append(field.getName()).append(" BLOB,");
                } else {
                    //不支持的类型
                    continue;
                }
            }
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }

```

伏笔：如何加快数据库查询速度

## 3. 封装插入数据

我们知道插入一条数据需要知道字段和字段的值

伪代码如下

```java
   public long insert(T entity) {
       // 准备好ContentValues中需要用的数据
        Map<String, String> map = getValues(entity);
        //设置插入的内容
        ContentValues values = getContentValues(map);
        //执行插入
        return mSqLiteDatabase.insert(mTableName, null, values);
    }
```

我们希望拿到ContentValues的key和value，来执行我们的插入操作

key从哪里来？

可以通过查询数据库

```java
 Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
 String[] columnNames = cursor.getColumnNames();
```

然后通过反射和注解拿到实体类的值

```java
Field[] columnFields = mEntityClass.getDeclaredFields();
String fieldName = null;
 if (field.getAnnotation(DbField.class) != null) {
 		fieldName = field.getAnnotation(DbField.class).value();
 } else {
        fieldName = field.getName();
}
```

这样看起来并没有什么问题



可是数据量比较大的时候呢，每次都反射，肯定影响效率啊，后面我们会实际测试



下面**知识点**来了

我们如何缓存实体类的属性与数据库字段之间的对应关系，

缓存一下这种关系

好吧，我们可以在创建表的时候缓存

```java
// 数据库字段名作为 key  实体类的属性Field作为value 
private HashMap<String, Field> cacheMap;
```

```java
 // 缓存数据库字段--->加快查询对应关系的速度
    private void cacheRelationship() {
        try {
            //1.取所有的列名====(查空表)
            String sql = "select * from " + mTableName + " limit 1,0";
            Cursor cursor = mSqLiteDatabase.rawQuery(sql, null);
            String[] columnNames = cursor.getColumnNames();
            //2.取所有的成员变量(反射)
            Field[] columnFields = mEntityClass.getDeclaredFields();
            //3.进行列名和成员变量的映射,存入到缓存中
            for (Field field : columnFields) {
                field.setAccessible(true);
            }
            for (String columnName : columnNames) {
                Field columnFiled = null;
                for (Field field : columnFields) {
                    String fieldName = null;
                    if (field.getAnnotation(DbField.class) != null) {
                        fieldName = field.getAnnotation(DbField.class).value();
                    } else {
                        fieldName = field.getName();
                    }
                    if (columnName.equals(fieldName)) {
                        columnFiled = field;
                        break;
                    }
                }
                if (columnFiled != null) {
                    cacheMap.put(columnName, columnFiled);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "cacheRelationship:" + e.toString());
        }


    }

```

在第2步创建表的地方缓存一下就可以了

现在数据有了

开始插入数据

获取对应关系

```java
   private Map<String, String> getValues(T entity) {
        HashMap<String, String> map = new HashMap<>();
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()) {
            Field field = fieldIterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                Object object = field.get(entity);
                if (object == null) {
                    continue;
                }
                String value = object.toString();
                //获取列名
                String key;
                if (field.getAnnotation(DbField.class) != null) {
                    key = field.getAnnotation(DbField.class).value();
                } else {
                    key = field.getName();
                }
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    map.put(key, value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
```

转换为ContentValues

```java
    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        for (Object key1 : keys) {
            String key = (String) key1;
            String value = map.get(key);
            if (value != null) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }
```



咱们回看上面的伪代码，就可以了



## 4. 执行操作

写了半天，咱们还没有个入口程序

数据库初始化需要两个参数

```java
    // 数据库名称
    protected String mDbName;
    // 数据库路径
    protected String mDbPath;
```



初始化方法

```java
// dataBase实例
private SQLiteDatabase sdb;

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

```

获取Dao实例，需要考虑同步问题

```java
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
```



界面插入数据

```java
User user = new User();
user.setId("n000" + i);
user.setPassword("123456");
user.setName("张三" + (++i));
// 数据库增加一条数据
BaseDao<User> baseDao = DaoFactory.getInstance().getBaseDao(User.class);
long insert = baseDao.insert(user);
Log.e("dds_test", "返回结果：" + insert);

```



今天先这样吧，完成第一步



## 详细代码

https://github.com/ddssingsong/AnyTool






















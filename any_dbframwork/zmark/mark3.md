做即时通讯做的久了，老是想着各种优化的问题

而数据库优化可以让app变的飞快，同时让开发更简单

前面已经有两篇

- [Android徒手撸数据库系列——注解与反射数据库关系模型](https://blog.csdn.net/u011077027/article/details/95646227)

- [Android徒手撸数据库系列——实现单表的增删改查](https://blog.csdn.net/u011077027/article/details/95987608)

下面主要介绍多用户数据库的分库实现



## 1. 新建总控用户User

```java
@DbTable("tb_user")
public class User {
    @DbField("_id")
    private String id;
    @DbField("name")
    private String name;
    @DbField("pwd")
    private String password;
    // 登录状态
    private Integer state;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
```

## 2. 编写登录逻辑

登录状态

```java
 enum State {Default, Login}
```

继承BaseDao

```java
public class UserDao extends BaseDao<User> {
    
    private final static String TAG = "dds_UserDao";
    // 登录状态
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
```



## 3. DaoFactory添加子逻辑

我们需要获取到UserDao的实例对象，可以使用反射的方式获取

```java
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
```



## 4. 测试登录用户

```java
 // 测试登陆和切换用户
    public void onLogin1(View view) {
        User user = new User();
        user.setPassword("123456");
        user.setName("用户1");
        user.setId("n0001");
        userDao.insert(user);
        // 显示登录用户
        User currentUser = userDao.getCurrentUser();
        textView.setText(currentUser.getName());
    }
```

查看数据库里的内容，我们可以看到用户1已经被插入到数据库中，这时，我们的总控用户就完成了它的使命，下面我们就开始各个用户在相应的文件夹下创建数据库并操作

## 5. 用户具体分库事项

 分库的主要的步骤为

1. 登录用户
2. 查询用户文件夹和用户数据库，如未创建，就创建
3. 构建Dao

```java
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

```

获取位置信息

```java

    public String getValue(String parentDir) {
        UserDao userDao = DaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
        if (userDao != null) {
            User currentUser = userDao.getCurrentUser();
            if (currentUser != null) {
                File file = new File(parentDir, currentUser.getId());
                if (!file.exists()) {
                    file.mkdirs();
                }
                return file.getAbsolutePath() + "/dds.db";
            }

        }
        return value;
    }

```

## 6. 测试向表中插入数据

因为获取存入的位置需要Context，需要先调用之前init方法，获取位置

```java

    // 设置插入数据
    public void onSubInsert(View view) {
        SubDaoFactory.getInstance().init(this, "dbTest.db");
        UserInfoDao photoDao = SubDaoFactory.getInstance().getSubDao(UserInfoDao.class, UserInfo.class);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(123456L);
        userInfo.setUserName("N1234");
        userInfo.setNickName("ddssingsong");
        userInfo.setMarkName("mark");
        photoDao.insert(userInfo);
    }
```



## 详细代码

https://github.com/ddssingsong/AnyTool
















做即时通讯app做的久了，老是想着各种优化的问题

下面主要介绍多用户数据库的分库实现

## 新建总控用户User

```java
@DbTable("tb_user")
public class User {
    @DbField("_id")
    private String id;
    @DbField("name")
    private String name;
    @DbField("pwd")
    private String password;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
```


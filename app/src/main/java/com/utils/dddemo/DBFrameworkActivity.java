package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.dbframwork.db.BaseDao;
import com.dds.dbframwork.db.DaoFactory;
import com.dds.dbframwork.sub_db.User;
import com.dds.dbframwork.sub_db.UserDao;

public class DBFrameworkActivity extends AppCompatActivity {

    private TextView textView;
    UserDao userDao;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, DBFrameworkActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbframework);
        textView = findViewById(R.id.textView);
        DaoFactory.getInstance().init(App.getApp(), "dbTest.db");

        userDao = DaoFactory.getInstance().getBaseDao(UserDao.class, User.class);
    }

    private int i = 0;

    // 测试登陆和切换用户
    public void onLogin1(View view) {
        User user = new User();
        user.setPassword("123456");
        user.setName("用户1");
        user.setId("n0001");

    }

    public void onLogin2(View view) {

        User user = new User();
        user.setPassword("123456");
        user.setName("用户2");
        user.setId("n0002");
    }


    public void onAdd(View view) {
        User user = new User();
        user.setId("n000" + i);
        user.setPassword("123456");
        user.setName("张三" + (++i));

        // 数据库增加一条数据
        BaseDao<User> baseDao = DaoFactory.getInstance().getBaseDao(User.class);
        long insert = baseDao.insert(user);
        Log.e("dds_test", "返回结果：" + insert);

    }


}

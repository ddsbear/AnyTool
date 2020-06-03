package com.utils.dddemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.dbframwork.db.DaoFactory;
import com.dds.dbframwork.sub_db.SubDaoFactory;
import com.dds.dbframwork.sub_db.User;
import com.dds.dbframwork.sub_db.UserDao;
import com.utils.dddemo.App;
import com.utils.dddemo.R;
import com.utils.dddemo.db.UserInfo;
import com.utils.dddemo.db.UserInfoDao;

public class DBFrameworkActivity extends AppCompatActivity {

    private TextView textView;
    private UserDao userDao;

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

        User currentUser = userDao.getCurrentUser();
        if (currentUser != null) {
            textView.setText("当前登录用户：" + currentUser.getName());
        }
    }


    // 测试登陆和切换用户
    public void onLogin1(View view) {
        User user = new User();
        user.setPassword("123456");
        user.setName("用户1");
        user.setId("n0001");
        userDao.insert(user);
        // 显示登录用户
        User currentUser = userDao.getCurrentUser();
        textView.setText("当前登录用户：" + currentUser.getName());
    }

    public void onLogin2(View view) {
        User user = new User();
        user.setPassword("123456");
        user.setName("用户2");
        user.setId("n0002");
        userDao.insert(user);
        User currentUser = userDao.getCurrentUser();
        textView.setText("当前登录用户：" + currentUser.getName());
    }

    // 测试插入数据
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

    // 测试升级
    public void onUpdate(View view) {

    }
}

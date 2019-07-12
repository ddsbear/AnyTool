package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.dds.dbframwork.db.BaseDao;
import com.dds.dbframwork.db.DaoFactory;
import com.utils.dddemo.db.User;

public class DBFrameworkActivity extends AppCompatActivity {

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, DBFrameworkActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbframework);
        DaoFactory.getInstance().init(App.getApp(), "dbTest.db");
    }

    private int i = 0;

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

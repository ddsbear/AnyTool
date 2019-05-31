package com.trustmobi.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.trustmobi.dddemo.hack.HackDemo;
import com.trustmobi.library.Hack;
import com.trustmobi.library.Toasts;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HackActivity extends AppCompatActivity {

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, HackActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack);
    }

    public void onHook(View view) {
        // hack constructor
        Hack.HackedMethod1<HackDemo, Void, IOException, Hack.Unchecked, Hack.Unchecked, Integer> constructor
                = Hack.into(HackDemo.class)
                .constructor()
                .throwing(IOException.class)
                .withParam(int.class);

        assertNotNull(constructor);
        try {

            HackDemo simple = constructor
                    .invoke(5)
                    .statically();
            assertNotNull(simple);

            // hack method
            final Hack.HackedMethod0<Integer, HackDemo, Hack.Unchecked, Hack.Unchecked, Hack.Unchecked> foo
                    = Hack
                    .into(HackDemo.class)
                    .method("foo")
                    .returning(int.class)
                    .withoutParams();

            assertNotNull(foo);

            assertEquals(7, (int) foo.invoke().on(simple));

            Toasts.showShort(this, "测试成功");
        } catch (IOException e) {

            Toasts.showShort(this, "IOException");
        }


    }
}

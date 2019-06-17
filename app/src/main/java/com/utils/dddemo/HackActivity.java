package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.utils.dddemo.hack.HackDemo;
import com.utils.library.Hack;
import com.utils.library.Toasts;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class HackActivity extends AppCompatActivity {

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, HackActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hack);
        Hack.setAssertionFailureHandler(failure -> mFailure = failure);
    }

    public void onHook(View view) {
        // --------hack constructor
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

            // -----------hack method
            final Hack.HackedMethod0<Integer, HackDemo, Hack.Unchecked, Hack.Unchecked, Hack.Unchecked> foo
                    = Hack
                    .into(HackDemo.class)
                    .method("foo")
                    .returning(int.class)
                    .withoutParams();

            assertNotNull(foo);

            assertEquals(7, (int) foo.invoke().on(simple));

            // -----------hack method
            Hack.HackedMethod0<Integer, HackDemo, RuntimeException, Hack.Unchecked, Hack.Unchecked> foo_rt_ex
                    = Hack
                    .into(HackDemo.class)
                    .method("foo")
                    .returning(int.class)
                    .throwing(RuntimeException.class)
                    .withoutParams();


            assertNotNull(foo_rt_ex);

            assertEquals(7, (int) foo_rt_ex.invoke().on(simple));

            // -----------hack method
            final Hack.HackedMethod0<Integer, HackDemo, IOException, Hack.Unchecked, Hack.Unchecked> foo_ex
                    = Hack
                    .into(HackDemo.class)
                    .method("foo")
                    .returning(int.class)
                    .throwing(IOException.class)
                    .withoutParams();

            assertNotNull(foo_ex);

            assertEquals(7, (int) foo_ex.invoke().on(simple));

            // -----------hack staticMethod
            final Hack.HackedMethod3<Void, Void, IOException, Hack.Unchecked, Hack.Unchecked, Integer, String, HackDemo> bar
                    = Hack.into(HackDemo.class)
                    .staticMethod("bar")
                    .throwing(IOException.class)
                    .withParams(int.class, String.class, HackDemo.class);

            assertNotNull(bar);

            bar.invoke(-1, "xyz", simple).statically();

            // -----------error hack
            assertFail(null,
                    Hack.into(HackDemo.class)
                            .method("bar")
                            .throwing(UnsupportedOperationException.class, FileNotFoundException.class)
                            .withParams(int.class, String.class, HackDemo.class));

            assertFail(NoSuchMethodException.class,
                    Hack.into(HackDemo.class)
                            .method("notExist")
                            .withoutParams());

            assertFail(NoSuchMethodException.class,
                    Hack.into(HackDemo.class)
                            .method("foo")
                            .withParam(int.class));
            assertFail(null,
                    Hack.into(HackDemo.class)
                            .staticMethod("foo")
                            .withoutParams());
            assertFail(null,
                    Hack.into(HackDemo.class)
                            .method("foo")
                            .returning(Void.class)
                            .withoutParams());


            Toasts.showShort(this, "Hook Test success !");


        } catch (IOException e) {

            Toasts.showShort(this, "IOException");
        }


    }

    private Hack.AssertionException mFailure;

    private void assertFail(final Class<? extends Throwable> failure, final Object hack) {
        assertNull(hack);
        assertNotNull(mFailure);
        if (failure != null) {
            assertNotNull(mFailure.getCause());
            assertEquals(failure, mFailure.getCause().getClass());
        }
        mFailure = null;
    }
}

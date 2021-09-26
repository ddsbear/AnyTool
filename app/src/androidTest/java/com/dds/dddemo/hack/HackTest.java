package com.dds.dddemo.hack;

import androidx.test.runner.AndroidJUnit4;

import com.dds.common.hack.Hack;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dds on 2019/10/18.
 * android_shuai@163.com
 */
@RunWith(AndroidJUnit4.class)
public class HackTest {
    @Test
    public void hookConstructorMethod() throws Throwable {
        // hook 构造方法，然后执行里面的方法
//        Hack.HackedMethod0 constructor = Hack
//                .into("com.utils.dddemo.hack.HackDemo")
//                .constructor()
//                .withoutParams();
//        assertNotNull(constructor);
//
//        Object statically = constructor
//                .invoke()
//                .statically();
//
//        assertNotNull(statically);

        Hack.HackedMethod1 constructor1 = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withParam(int.class);

        assertNotNull(constructor1);

        Object statically1 = constructor1.invoke(1222).statically();

        assertNotNull(statically1);
    }

    @Test
    public void hookMethod() throws Throwable {
//        Hack.HackedMethod1 constructor1 = Hack
//                .into("com.utils.dddemo.hack.HackDemo")
//                .constructor()
//                .withParam(int.class);
//
//        assertNotNull(constructor1);
//
//        Object statically1 = constructor1.invoke(1222).statically();
//
//        assertNotNull(statically1);
//
//        Hack.HackedMethod0 foo = Hack
//                .into("com.utils.dddemo.hack.HackDemo")
//                .method("foo")
//                .returning(int.class)
//                .withoutParams();
//
//        assertNotNull(foo);
//
//        assertEquals(1222, (int) foo.invoke().on(statically1));

        Hack.HackedMethod1 constructor1 = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withParam(int.class);

        assertNotNull(constructor1);

        Object statically1 = constructor1.invoke(1222).statically();

        assertNotNull(statically1);

        Hack.HackedMethod2 foo = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .method("foo")
                .returning(int.class)
                .withParams(int.class, String.class);

        assertNotNull(foo);

        assertEquals(7, (int) foo.invoke(11, "dds_test").on(statically1));
    }

    @Test
    public void hookStaticMethod() throws Throwable {
        // hook 静态方法 无参数 无返回值
//        Hack.HackedMethod0 method = Hack
//                .into("com.utils.dddemo.hack.HackDemo")
//                .staticMethod("bar")
//                .withoutParams();
//        assertNotNull(method);
//
//        method.invoke().statically();


        Hack.HackedMethod3 method
                = Hack.into("com.utils.dddemo.hack.HackDemo")
                .staticMethod("bar")
                .throwing(IOException.class)
                .withParams(int.class, String.class, Bean.class);

        assertNotNull(method);

        method.invoke(-1, "xyz", new Bean()).statically();
    }

    @Test
    public void hookStaticField() throws Throwable {
        Hack.HackedTargetField field = Hack.into("com.utils.dddemo.hack.HackDemo").staticField("staticField").ofType(String.class);
        field.set("dds111111111");
        Hack.HackedMethod0 constructor = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withoutParams();

        assertNotNull(constructor);

        Object statically = constructor.invoke().statically();
        ((HackDemo) statically).printStaticField();


    }
}

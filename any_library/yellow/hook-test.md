## Android 如何优雅的hook私有方法

最近在使用java写反射进行hook系统的方法，但是每次都会写一长串代码，各种try catch，着实让人的着急，偶然间想起来从一个开源库里看到的一个类，当时没看懂，就保存了下来，现在回头看看居然很有意思

[Hack.java](https://github.com/ddssingsong/AnyTool/blob/master/any_library/src/main/java/com/utils/library/hack/Hack.java)

就是这个类

有几大特性

- 链式调用
- 简洁，清晰



下面我们来看这个类的使用方法

首先放一个类，咱们就来hack这个类

```java
package com.utils.dddemo.hack;
import android.util.Log;
import com.utils.library.log.LogA;
public class HackDemo {
    private int mIntField;
    private String mStr;
    private static String staticField = "dds";
    // 构造方法
    private HackDemo() {
        Log.d("dds", "constructor");
    }
    private HackDemo(int x) {
        mIntField = x;
        Log.d("dds", "constructor " + x);
    }
    private HackDemo(int x, String str) {
        mIntField = x;
        mStr = str;
        Log.d("dds", "constructor " + x);
    }
    
     // 成员方法
    private int foo() {
        Log.d("dds", "method :foo");
        // 返回私有变量
        return mIntField;
    }
    private int foo(int type, String str) {
        Log.d("dds", "method :foo " + type + "," + str);
        return 7;
    }
    
    // 静态成员方法
    private static void bar() {
        Log.d("dds", "static method :bar");
    }
    private static int bar(int type) {
        Log.d("dds", "static method :bar " + type);
        return type;
    }
    private static void bar(int type, String name, Bean bean) {
        LogA.d("dds", "static method :bar type:%d,%s,%s", type, name, bean.toString());
    }
    
    // 打印静态成员变量
    public void printStaticField() {
        Log.d("dds_test", "printStaticField:" + staticField);
    }
}

```



## 私有构造方法

无参数构造方法

```java
        构造
		Hack.HackedMethod0 constructor = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withoutParams();
		// check
        assertNotNull(constructor);
		// 调用
        Object statically = constructor
                .invoke()
                .statically();
		// check
        assertNotNull(statically);
```

有参数构造方法

```java
  		// 构造
		Hack.HackedMethod1 constructor1 = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withParam(int.class);
        // check
        assertNotNull(constructor1);
        // 调用
        Object statically1 = constructor1
        		.invoke(1222)
            	.statically();
        // check
        assertNotNull(statically1);
```



## 私有成员方法

无参数成员方法

```java
        Hack.HackedMethod1 constructor1 = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withParam(int.class);
		// check
        assertNotNull(constructor1);
		// 构造方法
        Object statically1 = constructor1.invoke(1222).statically();
     	// check
        assertNotNull(statically1);
        
		// 构造该方法
        Hack.HackedMethod0 foo = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .method("foo")
                .returning(int.class)
                .withoutParams();
		// check
        assertNotNull(foo);
        
 		// check 返回值是否等于传入的值
        assertEquals(1222, (int) foo.invoke().on(statically1));

```

有参数，有返回值方法

```java
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

```



## 私有静态方法

无参数静态方法

```java
Hack.HackedMethod0 method = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .staticMethod("bar")
                .withoutParams();
        assertNotNull(method);
		// 方法调用
        method.invoke().statically();
```



有参数静态方法

```java
        Hack.HackedMethod3 method
                = Hack.into("com.utils.dddemo.hack.HackDemo")
                .staticMethod("bar")
                .throwing(IOException.class)
                .withParams(int.class, String.class, Bean.class);

        assertNotNull(method);
		// 调用方法
        method.invoke(-1, "xyz", new Bean()).statically();
```



下面重点来了，我们经常hook系统的方法都是hook的静态成员变量，那我们该如何修改静态成员变量的呢？



## 静态成员变量

```java
		Hack.HackedTargetField field = Hack
			.into("com.utils.dddemo.hack.HackDemo")
    		.staticField("staticField")
    		.ofType(String.class);
		//设置新值
        field.set("dds111111111");
		
		//打印出新值
        Hack.HackedMethod0 constructor = Hack
                .into("com.utils.dddemo.hack.HackDemo")
                .constructor()
                .withoutParams();

        assertNotNull(constructor);
        
        Object statically = constructor.invoke().statically();
        ((HackDemo) statically).printStaticField();
```



打印出结果

```
D/dds_test: printStaticField:dds111111111
```



## 代码收录

[https://github.com/ddssingsong/AnyTool](https://github.com/ddssingsong/AnyTool)












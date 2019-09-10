优雅的解决Android6.0运行时权限问题

之前遇到6.0运行时权限问题的时候每次都要写一次`requestPermissions`和`checkPermission`或者`shouldShowRequestPermissionRationale`，然后在Activity的`onRequestPermissionsResult`的回调里的处理结果，每次都好烦。

看了第三方的请求权限的封装，每一个都好麻烦，各种反射各种无用的代码

突然发现了一种优雅的方式进行权限请求

这是我见过的最清新的解决方案了

调用方式如下：

```java
 Permissions.request(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, integer -> {
            if (integer == PackageManager.PERMISSION_GRANTED) {
                Toasts.showShort(MainActivity.this, "成功accept : " + integer);
            } else {
                Toasts.showShort(MainActivity.this, "失败accept : " + integer);
            }
        });
```



完整代码如下

```java
public class Permissions {


    /**
     * @param callback will be called if request is not canceled, with either
     *                 {@link PackageManager#PERMISSION_GRANTED} or {@link PackageManager#PERMISSION_DENIED}
     */
    public static void request(Activity activity, String permission, Consumer<Integer> callback) {
        if (Build.VERSION.SDK_INT >= M) {
            request2(activity, permission, callback);
        } else {
            if (has(activity, permission)) {
                callback.accept(0);
            } else {
                callback.accept(-1);
            }
        }

    }

    /**
     * @param callback will be called if request is not canceled, with either
     *                 {@link PackageManager#PERMISSION_GRANTED} or {@link PackageManager#PERMISSION_DENIED}
     */
    public static void request(Activity activity, String[] permissions, Consumer<Integer> callback) {
        if (Build.VERSION.SDK_INT >= M) {
            request2(activity, permissions, callback);
        } else {
            if (has(activity, permissions)) {
                callback.accept(0);
            } else {
                callback.accept(-1);
            }

        }

    }

    @RequiresApi(M)
    public static void request2(Activity activity, String permission, Consumer<Integer> callback) {
        final FragmentManager fm = activity.getFragmentManager();
        if (!has(activity, permission)) {
            fm.beginTransaction().add(new PermissionRequestFragment(new String[]{permission}, callback), null).commitAllowingStateLoss();
        } else {
            callback.accept(PERMISSION_GRANTED);
        }
    }


    @RequiresApi(M)
    public static void request2(Activity activity, String[] permissions, Consumer<Integer> callback) {
        final FragmentManager fm = activity.getFragmentManager();
        if (!has(activity, permissions)) {
            fm.beginTransaction().add(new PermissionRequestFragment(permissions, callback), null).commitAllowingStateLoss();
        } else {
            callback.accept(PERMISSION_GRANTED);
        }
    }

    private static boolean has(Context activity, String... permissions) {
        List<String> mPermissionListDenied = new ArrayList<>();
        for (String permission : permissions) {
            int result = checkPermission(activity, permission);
            if (result != PERMISSION_GRANTED) {
                mPermissionListDenied.add(permission);
            }
        }
        return mPermissionListDenied.size() == 0;
    }

    private static boolean has(Context context, String permission) {
        return context.checkPermission(permission, Process.myPid(), Process.myUid()) == PERMISSION_GRANTED;
    }

    private static int checkPermission(Context activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission);
    }

    @RequiresApi(M)
    public static class PermissionRequestFragment extends Fragment {

        @SuppressLint("ValidFragment")
        public PermissionRequestFragment(@NonNull final String[] permissions,
                                         @NonNull final Consumer<Integer> callback) {
            mPermissions = permissions;
            mCallback = callback;
        }

        @Override
        public void onCreate(@Nullable final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (mPermissions != null) requestPermissions(mPermissions, 0);

        }

        @Override
        public void onRequestPermissionsResult(final int request, @NonNull final String[] permissions,
                                               @NonNull final int[] results) {
            getFragmentManager().beginTransaction().remove(this).commit();
            if (mCallback == null || results.length == 0/* canceled */) return;
            boolean isGrant = true;
            for (int result : results) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isGrant = false;
                    break;
                }
            }
            mCallback.accept(isGrant ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED);
        }

        public PermissionRequestFragment() {
            mPermissions = null;
            mCallback = null;
        }

        private final @Nullable
        String[] mPermissions;
        private final @Nullable
        Consumer<Integer> mCallback;
    }

   
    public interface Consumer<T> {
        void accept(T t);
    }

}

```



## 代码收录

[https://github.com/ddssingsong/AnyTool](https://github.com/ddssingsong/AnyTool)


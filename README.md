# Overide
Here is a record of the common problems encountered in Android development and how they have been solved elegantly and simply



## Records

1. request permission

   ```java
     Permissions.request(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, 				Manifest.permission.CAMERA}, integer -> {
                   if (integer == PackageManager.PERMISSION_GRANTED) {
                       Toasts.showShort(MainActivity.this, "成功accept : " + integer);
                   } else {
                       Toasts.showShort(MainActivity.this, "失败accept : " + integer);
                   }
               });
   ```
   
   


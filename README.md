# Overide
Here is a record of the common problems encountered in Android development and how they have been solved elegantly and simply





## Records

1. permission

   ```java
    Permissions.request(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, integer -> {
                   if (integer == PackageManager.PERMISSION_GRANTED) {
                       VToast.show(MainActivity.this, "success accept : " + integer);
                   } else {
                       VToast.show(MainActivity.this, "failed accept : " + integer);
                   }
   
   
               });
   ```

   


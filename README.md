![logo](art/anytool2.png)

This project includes various tools in Android development. make development faster and easier

本项目收录Android开发中的各种工具，旨在让你的开发更加简单快速



## Instructions

**关于项目**

我们常常在思考，我们也常常在感叹，新技术层出不穷，而我们的学习也变得越来越枯燥无味。学习的方法是总结，而能让我们进步的是分享和交流。

这个项目是我的总结，我拿来分享，愿和你交流



## Modules

- About tools         各种工具
- About DB            各种DB工具和用法     
- About network   各种网络工具和用法
- About cipher       各种加解密



## details

### 1. any_library

这里放了一些常用的工具类

- File相关

  [FileDirUtil.java](any_library/src/main/java/com/utils/library/file/FileDirUtil.java)    文件目录工具
  
  [Fileutils.java](any_library/src/main/java/com/utils/library/file/FileUtils.java)        文件操作工具
  
- io相关

  [IoUtils.java](any_library/src/main/java/com/utils/library/io/IoUtils.java)
  
- 动态权限相关

  [Permissions.java](any_library/src/main/java/com/utils/library/permission/Permissions.java)   
  
- 接口类log日志打印接口类

  [LogA.java](any_library/src/main/java/com/utils/library/log/LogA.java)

- 弹框类

  [Toasts.java](any_library/src/main/java/com/utils/library/Toasts.java)

  [Dialogs.java](any_library/src/main/java/com/utils/library/Dialogs.java)

  [SnackBars.java](any_library/src/main/java/com/utils/library/snack/SnackBars.java)



**相关文章**

- [优雅的解决Android运行时权限问题](https://blog.csdn.net/u011077027/article/details/100694123)
- [优雅的hook私有方法](https://blog.csdn.net/u011077027/article/details/102630313)
- 如何优雅的使用代理模式封装网络请求和图片加载



### 2. db

手撸数据库框架

- [Android徒手撸数据库系列——注解与反射数据库关系模型](https://blog.csdn.net/u011077027/article/details/95646227)
- [Android徒手撸数据库系列——实现单表的增删改查](https://blog.csdn.net/u011077027/article/details/95987608)
- [Android徒手撸数据库系列——多用户分库的实现](https://blog.csdn.net/u011077027/article/details/96135613)




### 3. net 

相关文章





### 4. cipher

加解密模块: [cipher](any_cipher)

- AES 
- RSA
- Base64
- MD5
- 证书相关

相关文章

- [Android 合成pkcs12证书](https://blog.csdn.net/u011077027/article/details/100847057)
- [Android 生成pkcs10(csr)请求](https://blog.csdn.net/u011077027/article/details/100839294)
- [keytool命令详解--学习自签名证书就看这篇](https://blog.csdn.net/u011077027/article/details/100731436)
- [JNI AES加解密 c++和java互通详解](https://blog.csdn.net/u011077027/article/details/102757225)



# Override
Here is a record of the common problems encountered in Android development and how they have been solved elegantly and simply

本项目旨在优雅的解决Android开发中遇到的各种问题

## feature

- 基于androidx
- 模块分离，需要哪个功能就拷贝哪个
- 每一个问题都会配有一篇文章
- 请注意，这不仅仅是轮子

## Module

下面是各个模块的功能介绍，每个功能都介绍的非常详细

------------------------------------------------------------

### 1. any_library

这里放了一些常用的工具类

- File相关

  [FileDirUtil.java](any_library/src/main/java/com/utils/library/file/FileDirUtil.java)    文件目录工具
  
  - 获取沙箱内文件路径
  - 获取外部存储文件路径
  - 获取缓存目录
  - 获取下载目录
  
  [Fileutils.java](any_library/src/main/java/com/utils/library/file/FileUtils.java)    文件操作工具
  
  - 文件增、删、改、查、写入、各种骚操作
  
- io相关

  

**相关文章**

- [优雅的解决Android运行时权限问题](any_library/yellow/优雅的解决Android运行时权限问题.md)
- 优雅的hook私有方法
- 如何优雅的使用代理模式封装网络请求和图片加载





### 2. dbframework

手撸数据库框架

- [Android徒手撸数据库系列——注解与反射数据库关系模型](any2_dbframwork/zmark/mark1.md)
- [Android徒手撸数据库系列——实现单表的增删改查](any2_dbframwork/zmark/mark2.md)
- [Android徒手撸数据库系列——多用户分库的实现](any2_dbframwork/zmark/mark3.md)



### 3. UI

1. 大图拖拽返回效果实现


### 4. git教程
   [git教程](git_markdown/git_markdown.pptx)

### 5. 网络相关

```
[Android 生成p10请求]()

[Android 合成p12证书]()

[okHttp单向和双向请求]()
```

相关文章

- [keytool命令详解--学习自签名证书就看这篇](cipher/yellow/keytool命令详解.md)

- [Android端生成pkcs10请求]()

  

### 6. 加解密相关







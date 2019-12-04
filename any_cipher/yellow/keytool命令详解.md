## keytool命令详解

[TOC]

最近是又用到了自签名证书相关的内容，这里整理下keytool的使用

## keytool简介

**Keytool** 是一个Java 数据证书的管理工具 ,Keytool 将密钥（key）和证书（certificates）存在一个称为keystore的文件中。
在keystore里，包含两种数据：

1. **密钥实体**（Key entity）——密钥（secret key）又或者是私钥和配对公钥（采用非对称加密）

2. **可信任的证书实体**（trusted certificate entries）——只包含公钥

   

## keytool功能

使用下面命令可查看功能

```shell
ketytool --help
```

主要功能如下

```java
 -certreq            生成证书请求
 -changealias        更改条目的别名
 -delete             删除条目
 -exportcert         导出证书
 -genkeypair         生成密钥对
 -genseckey          生成密钥
 -gencert            根据证书请求生成证书
 -importcert         导入证书或证书链
 -importpass         导入口令
 -importkeystore     从其他密钥库导入一个或所有条目
 -keypasswd          更改条目的密钥口令
 -list               列出密钥库中的条目
 -printcert          打印证书内容
 -printcertreq       打印证书请求的内容
 -printcrl           打印 CRL文件的内容
 -storepasswd        更改密钥库的存储口令
```

我们从两条命令中来看一下命令参数

**第一条命令：创建证书库(keystore)并包含公私钥 并查看信息**

```shell
keytool -genkeypair \
        -alias ddssingsong \
        -keyalg RSA \
        -keysize 2048 \
        -keypass 123456 \
        -sigalg SHA256withRSA \
        -dname "cn=www.ddssingsong.com,ou=xxx,o=xxx,l=Beijing,st=Beijing,c=CN" \
        -validity 3650 \
        -keystore ddssingsong.p12 \
        -storetype PKCS12 \
        -storepass 123456
```

这里贴上可以复制粘贴的命令

```shell
keytool -genkeypair -alias ddssingsong -keyalg RSA -keysize 2048 -keypass 123456 -sigalg SHA256withRSA -dname "cn=ddssingsong,ou=xxx,o=xxx,l=Beijing,st=Beijing,c=CN" -validity 3650 -keystore ddssngong.p12 -storetype PKCS12 -storepass 123456
```

- alias产生别名，每个keystore都关联这一个独一无二的alias

- keyalg RSA 此处”RSA“为密钥的算法。可以选择的密钥算法有：RSA、DSA、EC。

- keysize 2048  密钥长度。keysize与keyalg默认对应关系：

  ```
  2048 (when using -genkeypair and -keyalg is “RSA”)
  1024 (when using -genkeypair and -keyalg is “DSA”)
  256 (when using -genkeypair and -keyalg is “EC”)
  ```

- keypass 指定别名条目的密码(私钥的密码)

- sigalg 签名算法

  - keyalg=RSA时，签名算法有：MD5withRSA、SHA1withRSA、SHA256withRSA、SHA384withRSA、SHA512withRSA。
  - keyalg=DSA时，签名算法有：SHA1withDSA、SHA256withDSA。
  - 此处需要注意：MD5和SHA1的签名算法已经不安全。

- dname 在此填写证书信息

  CN=名字与姓氏/域名,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码

- validity 3650 此处”3650“为证书有效期天数

- keystore  创建出的密钥生成路径，默认在当前目录创建证书库

- storetype 生成证书类型，可用的证书库类型为：JKS、PKCS12等。jdk9以前，默认为JKS。自jdk9开始，默认为PKCS12。

- storepass   指定密钥库的密码(获取keystore信息所需的密码)，最好与keypass 一致。

查看生成的证书

```
keytool -list  -v -keystore ddssingsong.p12 -storepass 123456
```

打印如下

```
C:\Users\dds\Desktop\ca>keytool -list -v -keystore ddssingsong.p12 -storepass 123456
密钥库类型: PKCS12
密钥库提供方: SUN

您的密钥库包含 1 个条目

别名: ddssingsong
创建日期: 2019-9-11
条目类型: PrivateKeyEntry
证书链长度: 1
证书[1]:
所有者: CN=mydomain, OU=xxx, O=xxx, L=Beijing, ST=Beijing, C=CN
发布者: CN=mydomain, OU=xxx, O=xxx, L=Beijing, ST=Beijing, C=CN
序列号: 291b04d1
有效期为 Wed Sep 11 09:30:36 CST 2019 至 Sat Sep 08 09:30:36 CST 2029
证书指纹:
         MD5:  3D:75:33:93:1C:BF:07:78:68:8E:54:7B:30:1B:EB:79
         SHA1: 32:F2:6C:1A:F0:A8:9A:E9:92:A2:AF:7F:2D:35:FA:37:FD:28:7C:BD
         SHA256: 0E:1E:7D:2E:CB:57:72:BC:7D:FE:C8:F0:24:48:2A:31:69:B2:D2:F2:2E:BC:03:9A:D4:15:BD:15:BE:5F:5F:2D
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展:

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 34 44 40 24 BA 6F 5D 7F   0A 45 BE C3 24 BD A7 C8  4D@$.o]..E..$...
0010: 9C 62 8C 9A                                        .b..
```

有时候需要查看base64的内容

```
keytool -list  -rfc -keystore ddssingsong.p12 -storepass 123456
```

打印如下

```
C:\Users\dds\Desktop\ca>keytool -list  -rfc -keystore ddssingsong.p12 -storepass 123456
密钥库类型: PKCS12
密钥库提供方: SUN

您的密钥库包含 1 个条目

别名: ddssingsong
创建日期: 2019-9-11
条目类型: PrivateKeyEntry
证书链长度: 1
证书[1]:
-----BEGIN CERTIFICATE-----
MIIDXzCCAkegAwIBAgIEKRsE0TANBgkqhkiG9w0BAQsFADBgMQswCQYDVQQGEwJD
TjEQMA4GA1UECBMHQmVpamluZzEQMA4GA1UEBxMHQmVpamluZzEMMAoGA1UEChMD
eHh4MQwwCgYDVQQLEwN4eHgxETAPBgNVBAMTCG15ZG9tYWluMB4XDTE5MDkxMTAx
MzAzNloXDTI5MDkwODAxMzAzNlowYDELMAkGA1UEBhMCQ04xEDAOBgNVBAgTB0Jl
aWppbmcxEDAOBgNVBAcTB0JlaWppbmcxDDAKBgNVBAoTA3h4eDEMMAoGA1UECxMD
eHh4MREwDwYDVQQDEwhteWRvbWFpbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCC
AQoCggEBALSFrqyYNse3A/UMnKF6+G13YsrOEWJ3ZSRevMmNqgdqC9sNjtx07jw7
AQImxsfvaRLyPWgIt7iJxIU4USn0ARctv/ambzUPGk30bkq/Lst8PZmJGp6xRTdK
jYkk/jqxIA56ChqKV4yd5ZbRAAXCt/rJxlKNCfg7/929xf9+8Topty96gohm3rXP
sMkkmFHjlVUTUnNstmL8UjseS1K3Yomg1CrZ/CP1mYFrn2av0JHW11wd5Crbkzni
izubzakjw9aDGRIsu4h6r5Dk+MiTDAszmYE7p2yvqFJNrkxxURAnZJ4H4AwtA84l
KYB1QvESc9vzH35OOQpiJVH9Gob1EBUCAwEAAaMhMB8wHQYDVR0OBBYEFDREQCS6
b11/CkW+wyS9p8icYoyaMA0GCSqGSIb3DQEBCwUAA4IBAQBdIpzxtgrcYBoTEkh5
hGJWsa55CPFF0pAWeS0lVC6hE/jvS8ujZC3ReCVVLtl7B+qakQVI7B8sOR+l4cAz
ns01/oWBAmOTUbsEUOaxHedCkxNOTKM1BppCxWcc6jZTtvf9F4ROuCR47jUk+v3Q
FJfV7XtKQtupFeS5XmZv+3hVtFoxL5RZWG0XjjfOIoQ+WoPaWxByEAmK3xAGZxVm
MBgGtv5QwQSJG7baEYTG5pVYM7wdPRjEC1ARnyBuhe/KBeWdpCXfeAtEmccNORov
C2JAvDZGv8Dwam+sIZm0iTNOwKJOmwRbj58k41QH5D1/nCZrA1TRLwCOlpW/Ejns
OA1/
-----END CERTIFICATE-----
```



**第二条命令：导出并打印证书信息**

```shell
keytool -export \
		-alias ddssingsong \
		-keystore ddssngong.p12 \
		-file rootca.crt \
		-storepass 123456
```

- file 输出证书文件路径

打印证书信息

```
keytool -printcert -file rootca.crt
windows可直接双击查看
```

打印如下

```
C:\Users\dds\Desktop\ca> keytool -printcert -file rootca.crt
所有者: CN=mydomain, OU=xxx, O=xxx, L=Beijing, ST=Beijing, C=CN
发布者: CN=mydomain, OU=xxx, O=xxx, L=Beijing, ST=Beijing, C=CN
序列号: 291b04d1
有效期为 Wed Sep 11 09:30:36 CST 2019 至 Sat Sep 08 09:30:36 CST 2029
证书指纹:
         MD5:  3D:75:33:93:1C:BF:07:78:68:8E:54:7B:30:1B:EB:79
         SHA1: 32:F2:6C:1A:F0:A8:9A:E9:92:A2:AF:7F:2D:35:FA:37:FD:28:7C:BD
         SHA256: 0E:1E:7D:2E:CB:57:72:BC:7D:FE:C8:F0:24:48:2A:31:69:B2:D2:F2:2E:BC:03:9A:D4:15:BD:15:BE:5F:5F:2D
签名算法名称: SHA256withRSA
主体公共密钥算法: 2048 位 RSA 密钥
版本: 3

扩展:

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: 34 44 40 24 BA 6F 5D 7F   0A 45 BE C3 24 BD A7 C8  4D@$.o]..E..$...
0010: 9C 62 8C 9A                                        .b..
]
]
```

## 其他命令

**jks转pkcs12**

```shell
keytool -importkeystore \
		-srckeystore keystore.jks \
		-srcstoretype JKS \
		-deststoretype PKCS12 \
		-destkeystore keystore.p12
```

**生成证书签名请求(CSR)**

   ```shell
   keytool -certreq -keyalg RSA \
           -alias ddssingsong \
           -keystore keystore.p12 \
           -storetype PKCS12 \
           -storepass 123456 \
           -file certreq.csr
   ```

**查看生成的CSR证书请求**

```shell
keytool -printcertreq -file certreq.csr
```

**使用CA根证书对证书请求进行签名**

**导入证书到证书库**

```shell
keytool -importcert -alias server -file server.crt -keystore server.p12 -storepass 123456
```

**更改密码**

```shell
keytool -storepasswd -new new_storepass -keystore keystore.p12
```










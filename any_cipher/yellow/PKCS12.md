## Android 生成PKCS12并导入证书

上回写到生成pkcs10证书传到后台，这篇主要写将签名后的证书导入到PKCS12中去

```java
   // 生成KeyPair
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();

    }

 // 合成p12证书
    public static void storeP12(PrivateKey pri, String p7, String p12Path, String p12Password) throws Exception {

        CertificateFactory factory = CertificateFactory.getInstance("X509");
        //初始化证书链
        X509Certificate p7X509 = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(p7.getBytes()));
        Certificate[] chain = new Certificate[]{p7X509};
        // 生成一个空的p12证书
        KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
        ks.load(null, null);
        // 将服务器返回的证书导入到p12中去
        ks.setKeyEntry("client", pri, p12Password.toCharArray(), chain);
        // 加密保存p12证书
        FileOutputStream fOut = new FileOutputStream(p12Path);
        ks.store(fOut, p12Password.toCharArray());
    }
```

## 代码收录

[https://github.com/ddssingsong/AnyTool](https://github.com/ddssingsong/AnyTool)

cipher模块
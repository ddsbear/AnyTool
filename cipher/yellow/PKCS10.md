## Android 生成PKCS10请求（csr）

在做到自签名的SSL的双向认证的时候，客户端需要生成p10请求传给服务器，服务器签名之后返回PKCS7证书信息，客户端使用本地密钥和PKCS7证书的合成PKCS12证书，就可以进行双向认证了，这里记录下p10证书的生成过程

引入三方jar包

```groovy
// 这个是加解密相关的包
implementation group: 'org.bouncycastle', name: 'bcprov-jdk15on', version: '1.62'
// 主要用到这个
implementation group: 'org.bouncycastle', name: 'bcpkix-jdk15on', version: '1.62'
```



完整代码如下

```java
  
  	// 生成KeyPair
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();

    }

	// 生成p10证书
    public static String generatePKCS10(KeyPair keyPair) throws Exception {
        // 证书签名算法
        String sigAlg = "SHA256withRSA";
        // 各种基本信息
        String params = "CN=CN,OU=Trustmobi,O=client3,L=BJ2";

        // CN和公钥
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                new X500Name(params), keyPair.getPublic());
        // 签名算法
        JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder(sigAlg);
        csBuilder.setProvider(new BouncyCastleProvider());

        ContentSigner signer = csBuilder.build(keyPair.getPrivate());
        // 生成PKCS10的二进制编码格式(ber/der)
        PKCS10CertificationRequest p10 = p10Builder.build(signer);

        //将二进制格式转换为证书格式(csr)
        PemObject pemObject = new PemObject("CERTIFICATE REQUEST", p10.getEncoded());
        StringWriter str = new StringWriter();
        JcaPEMWriter jcaPEMWriter = new JcaPEMWriter(str);
        jcaPEMWriter.writeObject(pemObject);
        jcaPEMWriter.close();
        str.close();
        // base64便于网络传输
        return Base64.encode(str.toString());
    }
```



## 代码收录

[https://github.com/ddssingsong/AnyTool](https://github.com/ddssingsong/AnyTool)
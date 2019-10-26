[TOC]

## 1. 前言

AES加密标准又称为高级加密标准[Rijndael](https://baike.baidu.com/item/Rijndael/5600037?fr=aladdin)加密法，是美国国家标准技术研究所NIST旨在取代DES的21世纪的加密标准。AES的基本要求是，采用对称分组密码体制，密钥长度可以为128、192或256位，分组长度128位，算法应易在各种硬件和软件上实现。

AES加密数据块和密钥长度可以是128b、192b、256b中的任意一个。AES加密有很多轮的重复和变换。大致步骤如下：

①密钥扩展（Key Expansion)；

②初始轮（InitialRound)；

③重复轮（Rounds），每一重复轮又包括字节间减法运算（SubBytes）、行移位（ShiftRows）、列混合（MixColurmns)、轮密钥加法运算（AddRoundKey)等操作；

①最终轮（Final Round)，最终轮没有列混合操作（MixColumns)。

关于AES加密的原理这里不再赘述，这里放个链接

[AES 加密算法的原理详解](https://blog.csdn.net/gulang03/article/details/81175854)



这篇文章我们主要讲实现，就拿jni和java来实现互通，下面我们以下面的规格进行编写代码

- AES/ECB/PKCS5Padding
- 加密后进行base64操作，防止乱码
- jni 使用 openssl库
- java 使用 javax.crypto.Cipher





## 2.  JNI实现

jni采用openssl为基础库，关于openssl的静态库编译，请看我的这篇文章

[使用clang编译openssl1.1.1d](https://blog.csdn.net/u011077027/article/details/102713175)

Android studio如何配置请直接看我代码

下面开始介绍AES的具体实现

首先定义号方法`cipher.h`

```c
class cipher {
public:
    static int aes_encrypt(unsigned char *in, unsigned char *key, int ketLen, unsigned char *out);

    static int aes_decrypt(unsigned char *in, unsigned char *key, int keyLen, unsigned char *out);

    static char *Base64encode(const void *pin, int inlen, char *pout, char bNewLine = 1);

    static void Base64decode(const char *pin, void *pout, int *poutLen, char bNewLine = 1);
};
```



java方法定义

```java
public native String AesEncode(String content, String key);

public native String AesDecode(String content, String key);
```



那么相应的我们在jni中就需要定义下面两个方法

```c
extern "C"
JNIEXPORT jstring JNICALL
Java_com_dds_openssl_OpenCipher_AesEncode(JNIEnv *env, jobject thiz,
                                          jstring _content,
                                          jstring _key)
                                          
extern "C"
JNIEXPORT jstring JNICALL
Java_com_dds_openssl_OpenCipher_AesDecode(JNIEnv *env, jobject thiz,
                                          jstring _content,
                                          jstring _key)                                          
```

然后是加密的实现

1. 第一步：获取key和需要加密的数据

   ```c
   // 需要加密的内容
   const char *content = env->GetStringUTFChars(_content, JNI_FALSE);
   // 需要加密内容的长度
   int contentLen = env->GetStringLength(_content);
   // 密钥 这个密钥长度必须是16的倍数
   const char *key = env->GetStringUTFChars(_key, JNI_FALSE);
   // 密钥长度
   int keyLen = env->GetStringLength(_key);
   ```

   

2. 获取加密完成后数据的长度

   ```c
    int encLen = contentLen + AES_BLOCK_SIZE - contentLen % AES_BLOCK_SIZE;
   ```

   这长度是怎么来的呢，下面这张图是java的填充和长度规则，所以要适配java，我们必须要按照规则来

   - 当加密内容长度小于16位，加密出来的内容为16位
   - 当加密内容长度等于16时，加密出来的内容长16+16=32位
   - 正好就是上面的等式
   
   ```shell
   算法/模式/填充              16字节加密后数据长度      不满16字节加密后长度
   AES/CBC/NoPadding           16                        不支持
   AES/CBC/PKCS5Padding        32                        16
   AES/CBC/ISO10126Padding     32                        16
   AES/CFB/NoPadding           16                        原始数据长度
   AES/CFB/PKCS5Padding        32                        16
   AES/CFB/ISO10126Padding     32                        16
   AES/ECB/NoPadding           16                        不支持
   AES/ECB/PKCS5Padding        32                        16
   AES/ECB/ISO10126Padding     32                        16
   AES/OFB/NoPadding           16                        原始数据长度
   AES/OFB/PKCS5Padding        32                        16
   AES/OFB/ISO10126Padding     32                        16
   AES/PCBC/NoPadding          16                        不支持
   AES/PCBC/PKCS5Padding       32                        16
   AES/PCBC/ISO10126Padding    32                        16 
   ```



3. 进行padding处理

   这里的padding我们使用PKCS5Padding，因为在各个平台，PKCS5Padding的支持最好，

   这个padding是啥呢

   简单点说就是，要加密的内容根据block_size进行分块之后，不足块大小的内容要进行补位，

   补位有的话，上面的表格已经列出来了

   我们用的PKCS5Padding就是在不足的地方用块剩余大小进行补位

   如：

   加密内容长12，我们需要补4个0x04

   加密内容长度为4，我们需要补12个0x12

   加密长度为16，我们需要的补16个0x16

   下面是我们的实现

   ```c
   /**
    * 
    * @param pSrc 需要padding的内容
    * @param nSrcLen 原文长度
    * @param kCodeLen 加密出内容长度
    */
   void Padding(unsigned char *pSrc, int nSrcLen, int kCodeLen) {
       if (nSrcLen < kCodeLen) {
           unsigned char ucPad = (unsigned char) (kCodeLen - nSrcLen);
           for (int nID = kCodeLen; nID > nSrcLen; --nID) {
               pSrc[nID - 1] = ucPad;
           }
       }
   }
   ```

   PKCS7Padding类似

   

4. 开始AES加密

   ```c
   int cipher::aes_encrypt(unsigned char *in,
                           unsigned char *key, int keyLen,
                           unsigned char *out) {
       AES_KEY aes;
       if (!in || !key || !out) return 0;
       // 检查key长度
       if (keyLen != 16 && keyLen != 24 && keyLen != 32) {
           LOGE("aes_encrypt key length is invalid");
           return -1;
       }
       // 设置key
       if (AES_set_encrypt_key(key, keyLen << 3, &aes) < 0) { // keyLen*8
           LOGE("aes_encrypt AES_set_encrypt_key error");
           return -2;
       }
       // ecb模式进行加密
       AES_ecb_encrypt(in, out, &aes, AES_ENCRYPT);
       return 1;
   }
   ```

5. base64处理

   ```c
   char *cipher::Base64encode(const void *pin, int inLen, char *pout, char bNewLine) {
       BIO *bMem, *b64;
       BUF_MEM *bptr;
   
       bMem = BIO_new(BIO_s_mem());
       b64 = BIO_new(BIO_f_base64());
       if (!bNewLine) {
           BIO_set_flags(b64, BIO_FLAGS_BASE64_NO_NL); //one line file
       }
       b64 = BIO_push(b64, bMem);
   
       BIO_write(b64, pin, inLen);
       BIO_flush(b64);
       BIO_get_mem_ptr(b64, &bptr);
   
       if (pout == NULL) {
           pout = (char *) malloc(bptr->length + 1);
       }
       memcpy(pout, bptr->data, bptr->length);
       pout[bptr->length] = 0;
   
       BIO_free_all(b64);
       return pout;
   }
   ```

   bNewLine设置为false，对应java中的defalut类型



6. 解密流程类似，这里只放代码

   ```c
   extern "C"
   JNIEXPORT jstring JNICALL
   Java_com_dds_openssl_OpenCipher_AesDecode(JNIEnv *env, jobject thiz,
                                             jstring _content,
                                             jstring _key) {
       LOGD("------------encrypt----------------");
       const char *content = env->GetStringUTFChars(_content, JNI_FALSE);
       int contentLen = env->GetStringLength(_content);
       const char *key = env->GetStringUTFChars(_key, JNI_FALSE);
       int keyLen = env->GetStringLength(_key);
       LOGD("dec content:%s", content);
       LOGD("dec key:%s", key);
       int encLen = 0;
       unsigned char *pEncData = (unsigned char *) malloc(contentLen);
       // base64解密
       cipher::Base64decode(content, pEncData, &encLen, false);
       LOGD("dec base64 hex :%s", char2HexStr(pEncData, encLen).c_str());
       int dataLen = encLen + 1;
       unsigned char *pData = (unsigned char *) malloc(dataLen);
       memset(pData, 0, dataLen);
       // ase 解密
       if (cipher::aes_decrypt(pEncData, (unsigned char *) key, keyLen, pData) <= 0) {
           free(pEncData);
           free(pData);
           env->ReleaseStringUTFChars(_content, content);
           env->ReleaseStringUTFChars(_key, key);
           return NULL;
       }
       pData[dataLen] = 0;
       LOGD("dec aes:%s", pData);
       jstring jStr = env->NewStringUTF((const char *) (pData));
       free(pEncData);
       free(pData);
       env->ReleaseStringUTFChars(_content, content);
       env->ReleaseStringUTFChars(_key, key);
       return jStr;
   }
   
   int cipher::aes_decrypt(unsigned char *in, unsigned char *key, int keyLen, unsigned char *out) {
       AES_KEY aes;
       if (!in || !key || !out) return 0;
       // 检查key长度
       if (keyLen != 16 && keyLen != 24 && keyLen != 32) {
           LOGE("aes_decrypt key length is invalid");
           return -1;
       }
       // 设置key
       if (AES_set_decrypt_key(key, keyLen << 3, &aes) != 0) { // keyLen*8
           LOGE("aes_decrypt AES_set_decrypt_key error");
           return -2;
       }
   
       AES_ecb_encrypt(in, out, &aes, AES_DECRYPT);
       return 1;
   }
   
   ```

   

## 3.  JAVA实现

java使用javax.crypto.Cipher实现，我们知道，在不同国家之间的密码算法的交流，出口的密钥长度是受限制的，国际标准AES的加密位数是128位，但是128位的加密强度并不能满足国内各位大佬们，尽管他们连128位的都懒得破解。

好了，具体怎么解决256位的问题，请看下面这片文章，这里不再赘述

[https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html](https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

下面先贴出来一个测试用例

```java
   @Test
    public void testAES2() throws Exception {
        String content = "123456";
        String key = "GAOQXQQ99QPKOMTZE9YF96OLTD8EU6T9";


        String encrypt = AESCrypt.encrypt(key, content,
                false, null,
                "AES/ECB/PKCS5Padding", null);
        Log.d(TAG, "加密出内容：" + encrypt);

        String decrypt = AESCrypt.decrypt(key, content,
                false, null,
                "AES/ECB/PKCS5Padding", null);
        Log.d(TAG, "解密出内容：" + decrypt);

        assertEquals("123456", decrypt.trim());


    }
```

我们可以看到java中的PKCS5Padding实现起来就比较简单了

1. 第一步 获取密钥

   ```java
       private static SecretKeySpec generateKey(final String password, boolean needDigest, String algorithm)
               throws NoSuchAlgorithmException, UnsupportedEncodingException {
           byte[] bytes;
           if (needDigest) {
               final MessageDigest digest = MessageDigest.getInstance(algorithm);
               byte[] bytesPwd = password.getBytes(CHARSET);
               digest.update(bytesPwd, 0, bytesPwd.length);
               bytes = digest.digest();
               log(algorithm + " key ", bytes);
           } else {
               bytes = password.getBytes(CHARSET);
               log("algorithm:" + algorithm + ",key ", bytes);
           }
   
   
           return new SecretKeySpec(bytes, "AES");
       }
   
   ```

2. 开始加密

   ```java
      /**
        * More flexible AES encrypt that doesn't encode
        *
        * @param key     AES key typically 128, 192 or 256 bit
        * @param iv      Initiation Vector
        * @param message in bytes (assumed it's already been decoded)
        * @return Encrypted cipher text (not encoded)
        * @throws GeneralSecurityException if something goes wrong during encryption
        */
       public static byte[] encrypt(final SecretKeySpec key, final byte[] message, final byte[] iv, String aes_mode)
               throws GeneralSecurityException {
           final Cipher cipher = Cipher.getInstance(aes_mode);
           if (aes_mode.contains("CBC")) {
               IvParameterSpec ivSpec = new IvParameterSpec(iv);
               cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
           } else {
               cipher.init(Cipher.ENCRYPT_MODE, key);
           }
           byte[] cipherText = cipher.doFinal(message);
           log("aesEnc", cipherText);
           return cipherText;
       }
   
   ```

   完整代码如下AESCrypt.java

   ```java
   public class AESCrypt {
       private static final String TAG = "dds_AESCrypt";
       private static final String CHARSET = "UTF-8";
   
       //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
       private static final byte[] ivBytes = {
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00};
   
       //togglable log option (please turn off in live!)
       public static boolean DEBUG_LOG_ENABLED = true;
   
   
       /**
        * Generates hash of the password which is used as key
        *
        * @param password   password
        * @param needDigest true:Generates hash of the password  false:no deal
        * @param algorithm  SHA1/SHA-256/MD5
        * @return SecretKeySpec
        */
       private static SecretKeySpec generateKey(final String password, boolean needDigest, String algorithm)
               throws NoSuchAlgorithmException, UnsupportedEncodingException {
           byte[] bytes;
           if (needDigest) {
               final MessageDigest digest = MessageDigest.getInstance(algorithm);
               byte[] bytesPwd = password.getBytes(CHARSET);
               digest.update(bytesPwd, 0, bytesPwd.length);
               bytes = digest.digest();
               log(algorithm + " key ", bytes);
           } else {
               bytes = password.getBytes(CHARSET);
               log("algorithm:" + algorithm + ",key ", bytes);
           }
   
   
           return new SecretKeySpec(bytes, "AES");
       }
   
   
       /**
        * Encrypt and encode message using 256-bit AES with key generated from password.
        *
        * @param password   used to generated key
        * @param message    the thing you want to encrypt assumed String UTF-8
        * @param needDigest true:Generates hash of the password  false:no deal
        * @param algorithm  SHA1/SHA-256/MD5
        * @return Base64 encoded CipherText
        * @throws GeneralSecurityException if problems occur during encryption
        */
       public static String encrypt(final String password, String message, boolean needDigest,
                                    String algorithm, String aes_mode, final byte[] iv)
               throws GeneralSecurityException {
   
           try {
               final SecretKeySpec key = generateKey(password, needDigest, algorithm);
               if (aes_mode.contains("NoPadding")) {
                   //不足16的倍数补空格
                   if (message.getBytes(CHARSET).length % 16 != 0) {
                       int tem = message.getBytes(CHARSET).length % 16;
                       StringBuilder messageBuilder = new StringBuilder(message);
                       for (int i = 0; i < 16 - tem; i++) {
                           messageBuilder.append(" ");
                       }
                       message = messageBuilder.toString();
                   }
               }
   
               log("input message", message);
   
               byte[] cipherText = encrypt(key, message.getBytes(CHARSET), iv, aes_mode);
   
               String encoded = new String(Base64.encode(cipherText), CHARSET);
   
               log("base64Enc", encoded);
               return encoded;
           } catch (UnsupportedEncodingException e) {
               if (DEBUG_LOG_ENABLED)
                   Log.e(TAG, "UnsupportedEncodingException ", e);
               throw new GeneralSecurityException(e);
           }
       }
   
   
       /**
        * More flexible AES encrypt that doesn't encode
        *
        * @param key     AES key typically 128, 192 or 256 bit
        * @param iv      Initiation Vector
        * @param message in bytes (assumed it's already been decoded)
        * @return Encrypted cipher text (not encoded)
        * @throws GeneralSecurityException if something goes wrong during encryption
        */
       public static byte[] encrypt(final SecretKeySpec key, final byte[] message, final byte[] iv, String aes_mode)
               throws GeneralSecurityException {
           final Cipher cipher = Cipher.getInstance(aes_mode);
           if (aes_mode.contains("CBC")) {
               IvParameterSpec ivSpec = new IvParameterSpec(iv);
               cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
           } else {
               cipher.init(Cipher.ENCRYPT_MODE, key);
           }
           byte[] cipherText = cipher.doFinal(message);
           log("aesEnc", cipherText);
           return cipherText;
       }
   
   
       /**
        * Decrypt and decode ciphertext using 256-bit AES with key generated from password
        *
        * @param password  used to generated key
        * @param base64Enc the encrpyted message encoded with base64
        * @return message in Plain text (String UTF-8)
        * @throws GeneralSecurityException if there's an issue decrypting
        */
       public static String decrypt(final String password, String base64Enc, boolean needDigest,
                                    String algorithm, String aes_mode, final byte[] iv)
               throws GeneralSecurityException {
   
           try {
               final SecretKeySpec key = generateKey(password, needDigest, algorithm);
   
               log("base64Enc", base64Enc);
               byte[] decodedCipherText = Base64.decode(base64Enc.getBytes(CHARSET));
               log("aesEnc", decodedCipherText);
   
               byte[] decryptedBytes = decrypt(key, decodedCipherText, ivBytes, aes_mode);
   
               String message = new String(decryptedBytes, CHARSET);
   
               log("output message", message);
   
   
               return message;
           } catch (UnsupportedEncodingException e) {
               if (DEBUG_LOG_ENABLED)
                   Log.e(TAG, "UnsupportedEncodingException ", e);
   
               throw new GeneralSecurityException(e);
           }
       }
   
   
       /**
        * More flexible AES decrypt that doesn't encode
        *
        * @param key               AES key typically 128, 192 or 256 bit
        * @param iv                Initiation Vector
        * @param decodedCipherText in bytes (assumed it's already been decoded)
        * @return Decrypted message cipher text (not encoded)
        * @throws GeneralSecurityException if something goes wrong during encryption
        */
       public static byte[] decrypt(final SecretKeySpec key, final byte[] decodedCipherText, final byte[] iv, String aes_mode)
               throws GeneralSecurityException {
           final Cipher cipher = Cipher.getInstance(aes_mode);
           if (aes_mode.contains("CBC")) {
               IvParameterSpec ivSpec = new IvParameterSpec(iv);
               cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
           } else {
               cipher.init(Cipher.DECRYPT_MODE, key);
           }
           return cipher.doFinal(decodedCipherText);
       }
   
   
       private static void log(String what, byte[] bytes) {
           if (DEBUG_LOG_ENABLED)
               Log.d(TAG, what + "[" + bytes.length + "] [" + bytesToHex(bytes) + "]");
       }
   
       private static void log(String what, String value) {
           if (DEBUG_LOG_ENABLED)
               Log.d(TAG, what + "[" + value.length() + "] [" + value + "]");
       }
   
       public static String bytesToHex(byte[] bytes) {
           final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                   '9', 'A', 'B', 'C', 'D', 'E', 'F'};
           char[] hexChars = new char[bytes.length * 2];
           int v;
           for (int j = 0; j < bytes.length; j++) {
               v = bytes[j] & 0xFF;
               hexChars[j * 2] = hexArray[v >>> 4];
               hexChars[j * 2 + 1] = hexArray[v & 0x0F];
           }
           return new String(hexChars);
       }
   
   }
   
   ```

   

## 代码收录

jni : [https://github.com/ddssingsong/AnyNdk](https://github.com/ddssingsong/AnyNdk)

java ： [https://github.com/ddssingsong/AnyTool](https://github.com/ddssingsong/AnyTool)












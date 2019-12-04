## any_cipher

本模块主要介绍使用java实现各种加解密

1. **AES**

   一般而言，AES的速度还是很快的，可用来加解密文件。加解密分为不同的模式和补位方式，这点需要注意，不然跨平台的时候无法互解

   ```java
   //=====================ECB NoPadding =================================
   String content = "欢迎来到 any_cipher";                                       
   String key = "123456"; 
   
   // 对key进行SHA-256加密，保证密码的位数是256位
   String encrypt = AESCrypt.encrypt(key, content,                           
           true, "SHA-256",                                                  
           "AES/ECB/NoPadding", null);                                       
                                                                             
   Log.d(TAG, "encrypt result-------------->：" + encrypt);                   
                                                                             
   String result = AESCrypt.decrypt(key, encrypt,                            
           true, "SHA-256",                                                  
           "AES/ECB/NoPadding", null);                                       
                                                                             
   Log.d(TAG, "decrypt result-------------->：" + result);                    
                                                                             
   assertEquals(result.trim(), content);                                     
   ```

   ```java
   private static final byte[] ivBytes = {
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00,
               0x00, 0x00, 0x00, 0x00};
   
   //==================CBC  PKCS5Padding ==================================
   
   String content = "欢迎来到any_cipher";
   String key = "123456";
   
   String encrypt1 = AESCrypt.encrypt(key, content,true, "SHA-256",
   				"AES/CBC/PKCS5Padding", ivBytes);
   
   Log.d(TAG, "result------------>：" + encrypt1);
   
   String result1 = AESCrypt.decrypt(key, encrypt1,true, "SHA-256",
                   "AES/CBC/PKCS5Padding", ivBytes);
   
   Log.d(TAG, "result------------>：" + result1);
   ```

   ```java
   @Test                                                                                              
   public void testAESFile() throws Exception {                                           
       Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();          
       File srcFile = new File(appContext.getFilesDir(), "test.txt");                                 
       // 创建一个文件并写入内容                                                             
       Utils.writeFile("hello world", srcFile.getAbsolutePath(), false);                              
       String key = "123456";                                                                         
       File encDir = new File(appContext.getFilesDir(), "a_enc");                         
       if (!encDir.exists()) {                                                           
           encDir.mkdirs();
       }                                                                                     // 开始加密        
       String s = AESCrypt.encryptFile(key, srcFile.getAbsolutePath(), encDir.getAbsolutePath(),      
               true, "SHA-256", "AES/ECB/PKCS5Padding", null);                                        
       if (s != null) {                                                                   
           String s1 = Utils.readFile(s); 
           // 打印加密结果
           Log.d(TAG, "enc result------>" + s1);                                                      
       }                                                                                              
       File decDir = new File(appContext.getFilesDir(), "a_dec");                         
       if (!decDir.exists()) {                                                           
           decDir.mkdirs();                                                               
       }                                                                                     // 开始解密         
       String s2 = AESCrypt.decryptFile(key, s, decDir.getAbsolutePath(),                 
               true, "SHA-256", "AES/ECB/PKCS5Padding", null);                                        
       if (s2 != null) {                                                                 
           String s3 = Utils.readFile(s2);                                               
           Log.d(TAG, "dec result------>" + s3);                                         
       }                                                                                 
   }                                                                                                  
   ```

   

2. **RSA**

   

3. base64

4. md5


package com.dds.cipher;

/**
 * Created by dds on 2018/5/31.
 * android_shuai@163.com
 */

public interface Cert {

    //生成p10证书请求
    String MakeX509Req(String prikey, String passwd, String pubkey, String[] info);

    //合成p12证书
    String CombineP12Cert(String prikey, String pass, String pubcert);


}

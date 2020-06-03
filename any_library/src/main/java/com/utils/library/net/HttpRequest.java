package com.utils.library.net;

import java.io.InputStream;
import java.util.Map;

/**
 * 网络请求接口封装
 * Created by dds on 2018/4/23.
 */

public interface HttpRequest {

    /**
     * 设置双向证书 服务器认证客户端
     *
     * @param certificate 证书
     * @param pwd         密码
     */
    void initCertificate(InputStream certificate, String pwd);

    /**
     * get请求
     *
     * @param url      url
     * @param params   params
     * @param callback callback
     */
    void get(String url, Map<String, String> params, ICallback callback);

    /**
     * post请求
     *
     * @param url      url
     * @param params   params
     * @param callback callback
     */
    void post(String url, Map<String, String> params, ICallback callback);


}

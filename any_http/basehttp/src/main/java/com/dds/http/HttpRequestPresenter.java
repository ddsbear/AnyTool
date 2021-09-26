package com.dds.http;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by dds on 2019/7/3.
 * android_shuai@163.com
 */
public class HttpRequestPresenter implements HttpRequest {
    protected HttpRequest httpRequest;
    private static volatile HttpRequestPresenter instance;

    public HttpRequestPresenter(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public static void init(HttpRequest httpRequest) {
        if (null == instance) {
            synchronized (HttpRequestPresenter.class) {
                if (null == instance) {
                    instance = new HttpRequestPresenter(httpRequest);
                }
            }
        }
    }

    public static HttpRequestPresenter getInstance() {
        return instance;
    }

    @Override
    public void get(String url, Map<String, String> params, ICallback callback) {
        httpRequest.get(url, params, callback);
    }

    @Override
    public void post(String url, Map<String, String> params, ICallback callback) {
        httpRequest.post(url, params, callback);
    }

    @Override
    public void initCertificate(InputStream certificate, String pwd) {
        httpRequest.initCertificate(certificate, pwd);
    }
}

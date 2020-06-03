package com.utils.library.net.okhttp;


import com.utils.library.net.HttpRequest;
import com.utils.library.net.ICallback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by dds on 2018/4/23.
 */

public class OkHttpRequest implements HttpRequest {
    private OkHttpClient.Builder okHttpClientBuilder;
    private OkHttpClient client;

    public OkHttpRequest() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null, null);
        // 忽略证书
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        this.client = okHttpClientBuilder.build();
    }

    @Override
    public void get(String url, Map<String, String> params, ICallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    String result = body.string();
                    callback.onSuccess(result);
                }
            } else {
                callback.onFailure(response.code(), new Throwable(response.message()));
            }
        } catch (IOException e) {
            callback.onFailure(-1, new Throwable("io error! please check your network"));
        }
    }

    @Override
    public void post(String url, Map<String, String> params, ICallback callback) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            builder.addFormDataPart(mapKey, mapValue);
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    String result = body.string();
                    callback.onSuccess(result);
                }
            }
        } catch (IOException e) {
            callback.onFailure(-1, new Throwable("io error! please check your network"));
        }

    }

    @Override
    public void initCertificate(InputStream certificate, String pwd) {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, certificate, pwd, null);
        // 忽略证书
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        this.client = okHttpClientBuilder.build();
    }
}

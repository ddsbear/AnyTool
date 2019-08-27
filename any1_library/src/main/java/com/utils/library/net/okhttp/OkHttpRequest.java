package com.utils.library.net.okhttp;


import com.utils.library.net.HttpRequest;
import com.utils.library.net.ICallback;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dds on 2018/4/23.
 */

public class OkHttpRequest implements HttpRequest {

    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
}

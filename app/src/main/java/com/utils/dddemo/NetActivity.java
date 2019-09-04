package com.utils.dddemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.cipher.CA;
import com.utils.library.net.HttpRequestPresenter;
import com.utils.library.net.ICallback;
import com.utils.library.net.okhttp.OkHttpRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetActivity extends AppCompatActivity {
    private KeyPair keyPair;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText text1;
    private EditText text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        HttpRequestPresenter.init(new OkHttpRequest());
    }

    public void get(View view) {

    }

    public void requestP10(View view) {
        executor.execute(() -> {
            try {
                // 生成密钥对
                keyPair = CA.generateKeyPair();
                // 生成p10请求
                String pks10 = CA.generatePKCS10(keyPair);
                String url = "";
                Map<String, String> map = new HashMap<>();
                map.put("p10", pks10);
                HttpRequestPresenter.getInstance().post(url, map, new ICallback() {
                    @Override
                    public void onSuccess(String result) {
                        Message message = new Message();
                        message.obj = result;
                        message.what = 1;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        Log.e("dds_error", t.toString());

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void post(View view) {
        executor.execute(() -> {
            try {
                // 生成密钥对
                keyPair = CA.generateKeyPair();
                // 生成p10请求
                String url = "";
                Map<String, String> map = new HashMap<>();
                map.put("p10", "");
                HttpRequestPresenter.getInstance().post(url, map, new ICallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("dds_test", result);
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        Log.e("dds_error", t.toString());

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    // 获取到p7证书，需要用这个证书生成p12证书进行认证
                    String p7 = (String) msg.obj;
                    PrivateKey aPrivate = keyPair.getPrivate();
                    try {
                        String path = getFilesDir().getAbsolutePath() + "/test.p12";
                        CA.storeP12(aPrivate, p7, path, "123456");
                        InputStream certificate = null;
                        File file = new File(path);
                        if (file.exists()) {
                            try {
                                certificate = new FileInputStream(file);
                                HttpRequestPresenter.getInstance().setCertificate(certificate, "123456");


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
}

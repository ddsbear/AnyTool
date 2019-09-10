package com.utils.dddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dds.cipher.CA;
import com.dds.cipher.base64.Base64;
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
    private EditText text3;

    private TextView text;
    private StringBuilder sb = new StringBuilder();

    public static void openActivity(Activity activity) {
        Intent intent = new Intent(activity, NetActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text = findViewById(R.id.text);
        HttpRequestPresenter.init(new OkHttpRequest());


        text1.setText("https://192.168.1.44/cert.php");
        text2.setText("https://192.168.1.44/1.php");
        text3.setText("https://192.168.1.44:4431/1.php");
    }

    public void requestP10(View view) {
        sb = new StringBuilder();
        sb.append("开始请求...").append("\n");
        text.setText(sb.toString());
        executor.execute(() -> {
            try {
                // 生成密钥对
                keyPair = CA.generateKeyPair();
                // 生成p10请求
                String pks10 = CA.generatePKCS10(keyPair);
                String url = text1.getText().toString();
                Map<String, String> map = new HashMap<>();
                map.put("p10", pks10);
                sb.append("url:").append(url).append("\n");
                sb.append("param:p10=").append(pks10).append("\n");
                Log.d("dds_test", sb.toString());
                HttpRequestPresenter.getInstance().post(url, map, new ICallback() {
                    @Override
                    public void onSuccess(String result) {
                        Message message = new Message();
                        message.obj = result;
                        message.what = 1;
                        if (TextUtils.isEmpty(result)) {
                            Log.d("dds_test", "返回值为空！");
                            return;
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        Log.e("dds_error", t.toString());
                        sb.append("双向建立失败：").append(t.toString()).append("\n");
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                });

            } catch (Exception e) {
                sb.append("双向建立失败：").append(e.toString()).append("\n");
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        });
    }

    public void loadP12(View view) {
        sb = new StringBuilder();
        sb.append("开始加载p12...").append("\n");
        text.setText(sb.toString());
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.p12";
        InputStream certificate;
        File file = new File(path);
        if (file.exists()) {
            try {
                certificate = new FileInputStream(file);
                HttpRequestPresenter.getInstance().setCertificate(certificate, "111111");
                sb.append("双向建立完成").append("\n");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                sb.append("双向建立失败").append(e.toString()).append("\n");
            }
        } else {
            sb.append("双向建立失败：sd/test.p12 no exist").append("\n");
        }
        text.setText(sb.toString());

    }

    public void post(View view) {
        sb = new StringBuilder();
        String url = text2.getText().toString();
        sb.append("开始请求接口").append("\n");
        sb.append(url).append("\n");
        text.setText(sb.toString());
        executor.execute(() -> {
            try {
                Map<String, String> map = new HashMap<>();
                HttpRequestPresenter.getInstance().get(url, map, new ICallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("dds_test", result);
                        sb.append("请求成功：" + result);
                        Message message = new Message();
                        message.obj = result;
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        Log.e("dds_error", t.toString());
                        sb.append("请求失败：" + t.toString());
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void post2(View view) {
        sb = new StringBuilder();
        String url = text3.getText().toString();
        sb.append("开始请求接口").append("\n");
        sb.append(url).append("\n");
        text.setText(sb.toString());
        executor.execute(() -> {
            try {
                Map<String, String> map = new HashMap<>();
                map.put("param", "param");
                HttpRequestPresenter.getInstance().get(url, map, new ICallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("dds_test", result);
                        sb.append("请求成功：").append(result).append("\n");
                        Message message = new Message();
                        message.obj = result;
                        message.what = 2;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(int code, Throwable t) {
                        Log.e("dds_error", t.toString());
                        sb.append("请求失败：").append(t.toString()).append("\n");
                        Message message = new Message();
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                });

            } catch (Exception e) {
                Log.e("dds_error", e.toString());
                sb.append("请求失败：").append(e.toString()).append("\n");
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
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
                    p7 = Base64.decode(p7);

                    PrivateKey aPrivate = keyPair.getPrivate();
                    sb.append("response result:p7=").append(p7).append("\n");
                    try {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.p12";
                        // 保存p12证书
                        CA.storeP12(aPrivate, p7, path, "111111");
                        InputStream certificate;
                        File file = new File(path);
                        if (file.exists()) {
                            try {
                                certificate = new FileInputStream(file);
                                HttpRequestPresenter.getInstance().setCertificate(certificate, "111111");
                                sb.append("双向建立成功").append("\n");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                sb.append("双向建立失败：" + e.toString()).append("\n");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        sb.append("双向建立失败：").append(e.toString()).append("\n");
                    }

                    text.setText(sb.toString());
                    break;

                case 2:
                    text.setText(sb.toString());
                    break;
            }
        }
    };


}

package com.utils.library.net.urlconn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by dds on 2019/11/28.
 * android_shuai@163.com
 */
public class UrlConnUtils {
    private static final String TAG = "dds_UrlConnUtils";

    public static String sendPost(String u, String json) {
        StringBuilder sbf = new StringBuilder();
        try {
            URL url = new URL(u);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            trustAllHosts(connection);
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            if (!"".equals(json)) {
                out.writeBytes(json);
            }
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbf.append(lines);
            }
            reader.close();
            connection.disconnect();
        } catch (IOException e) {
            return null;
        }
        return sbf.toString();
    }

    public static boolean download(String u, String path) {
        try {
            URL url = new URL(u);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            trustAllHosts(connection);
            connection.setHostnameVerifier(DO_NOT_VERIFY);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            //可设置请求头
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            connection.setRequestProperty("Charset", "UTF-8");
            connection.connect();
            byte[] file = input2byte(connection.getInputStream());
            File file1 = writeBytesToFile(file, path);
            if (file1.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    private static File writeBytesToFile(byte[] b, String outputFile) {
        File file = null;
        FileOutputStream os = null;
        try {
            file = new File(outputFile);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            os = new FileOutputStream(file);
            os.write(b);
        } catch (Exception var13) {
            var13.printStackTrace();
            if (file != null && file.exists()) {
                file.delete();
            }
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }
        return file;
    }

    public static String builderUrlParams(Map<String, Object> params) {
        JSONObject jsonObject;
        try {
            Set<String> keySet = params.keySet();
            List<String> keyList = new ArrayList<>(keySet);
            Collections.sort(keyList);
            jsonObject = new JSONObject();
            for (String key : keyList) {
                Object value = params.get(key);
                if (value == null || "".equals(value)) {
                    continue;
                }
                jsonObject.put(key, String.valueOf(params.get(key)));
            }
        } catch (JSONException e) {
            return null;
        }
        return jsonObject.toString();
    }

    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};

    private static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;
}

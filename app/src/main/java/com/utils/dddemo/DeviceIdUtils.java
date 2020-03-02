package com.utils.dddemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by dds on 2020/3/2.
 * android_shuai@163.com
 * <p>
 * 获取设备唯一id
 */
public class DeviceIdUtils {
    private static final String TAG = DeviceIdUtils.class.getSimpleName();
    public static String token = null;

    public static String getToken(Context context) {
        if (token == null) {
            SharedPreferences serialToken = context.getSharedPreferences("serialToken", Context.MODE_PRIVATE);
            String token = serialToken.getString("token", "");
            if (TextUtils.isEmpty(token)) {
                String deviceID = getUUID(context);
                String androidID = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);

                String substring = getMD5Str(deviceID + androidID).substring(0, 16);
                serialToken.edit().putString("token", substring).apply();
                return substring;
            }
            return token;
        }
        return token;

    }

    private static String getUUID(Context context) {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    serial = "";
                } else {
                    serial = android.os.Build.getSerial();
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager == null) {
                    return null;
                }
                @SuppressLint("HardwareIds") String imei = telephonyManager.getDeviceId();
                serial = imei;
            }
            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    private static String getMD5Str(String plainText) {
        MessageDigest md = null;
        StringBuilder buf = new StringBuilder();
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte[] b = md.digest();
            int i;
            for (byte value : b) {
                i = value;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();

    }

}

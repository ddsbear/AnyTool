package com.dds.http.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络相关
 */
public class Apn {
    public static final int APN_UNKNOWN = 0;
    public static final int APN_2G = 1;
    public static final int APN_3G = 2;
    public static final int APN_WIFI = 3;
    public static final int APN_4G = 4;

    public Apn() {
    }

    // 获取网络类型 String
    public static String getApnInfo(Context var0) {
        String var1 = "unknown";
        ConnectivityManager var2 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var3 = var2.getActiveNetworkInfo();
        if (var3 != null && var3.isConnectedOrConnecting()) {
            switch (var3.getType()) {
                case 0:
                    var1 = var3.getExtraInfo();
                    break;
                case 1:
                    var1 = "wifi";
            }
        }

        return var1;
    }

    // 获取网络类型 int
    public static int getApnType(Context var0) {
        byte var1 = 0;
        ConnectivityManager var2 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var3 = null;
        if (var2 != null) {
            var3 = var2.getActiveNetworkInfo();
        }
        if (var3 != null && var3.isConnectedOrConnecting()) {
            switch (var3.getType()) {
                case 0:
                    switch (var3.getSubtype()) {
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                        case 11:
                            var1 = 1;
                            return var1;
                        case 3:
                        case 5:
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                            var1 = 2;
                            return var1;
                        case 13:
                            var1 = 4;
                            return var1;
                        default:
                            var1 = 0;
                            return var1;
                    }
                case 1:
                    var1 = 3;
                    break;
                default:
                    var1 = 0;
            }
        }

        return var1;
    }

    // 网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager var1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = null;
        if (var1 != null) {
            var2 = var1.getActiveNetworkInfo();
        }
        return var2 != null && (var2.isConnected() || var2.isAvailable());
    }

    // 判断Wifi连接是否可用
    public static boolean isWifiActive(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (int i = 0; i < infos.length; i++) {
                    if (infos[i].getTypeName().equals("WIFI")
                            && infos[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 获取wifi的SSID
    public static String getWifiSSID(Context context) {
        try {
            String var1 = null;
            WifiManager var2 = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo var3 = var2.getConnectionInfo();
            if (var3 != null) {
                var1 = var3.getBSSID();
            }

            return var1;
        } catch (Throwable var4) {
            var4.printStackTrace();
            return "";
        }
    }

    //判断网络是否为漫游
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null
                    && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && tm.isNetworkRoaming()) {
                    return true;
                }
            }
        }
        return false;
    }

    // 获取IPv4地址
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {// 并保证读取的是ip4地址
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取MAC地址
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

}


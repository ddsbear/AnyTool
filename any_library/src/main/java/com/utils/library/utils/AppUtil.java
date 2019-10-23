package com.utils.library.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.utils.library.log.LogB;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 获取关于设备的信息
 */
public class AppUtil {
    private static final String TAG = "dds_AppUtils";
    public static String deviceId = "";
    public static String simId = "";
    public static String cpu = "";
    public static String wifiMac = "";
    public static String androidId = "";

    public static String getVerName(Context context) {
        String var1 = null;
        try {
            String var2 = context.getPackageName();
            PackageManager var3 = context.getPackageManager();
            PackageInfo var4 = var3.getPackageInfo(var2, 0);
            var1 = var4.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return var1;
    }

    public static int getVerCode(Context context) {
        int var1 = 0;
        try {
            String var2 = context.getPackageName();
            PackageManager var3 = context.getPackageManager();
            PackageInfo var4 = var3.getPackageInfo(var2, 0);
            var1 = var4.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return var1;
    }

    public static String getMetaData(Context context, String key) {
        String var2 = null;
        try {
            String var3 = context.getPackageName();
            PackageManager var4 = context.getPackageManager();
            ApplicationInfo var5 = var4.getApplicationInfo(var3, PackageManager.GET_META_DATA);
            var2 = String.valueOf(var5.metaData.get(key));
            try {
                int var6 = Integer.parseInt(var2);
                var2 = Integer.toHexString(var6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return var2;
    }

    public static String getDeviceId(Context var0) {
        String var1 = "";
        if (!TextUtils.isEmpty(deviceId)) {
            var1 = deviceId;
        } else {
            try {
                TelephonyManager var2 = (TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE);
                var1 = var2.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return var1;
    }

    public static String getSimId(Context var0) {
        String var1 = "";
        if (!TextUtils.isEmpty(simId)) {
            var1 = simId;
        } else {
            try {
                TelephonyManager var2 = (TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE);
                var1 = var2.getSubscriberId();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        return var1;
    }

    public static String getCpu() {
        if (!TextUtils.isEmpty(cpu)) {
            return cpu;
        } else {
            String var0 = null;
            InputStreamReader var1 = null;
            BufferedReader var2 = null;
            try {
                Process var3 = Runtime.getRuntime().exec("getprop ro.product.cpu.abi");
                var1 = new InputStreamReader(var3.getInputStream());
                var2 = new BufferedReader(var1);
                String var4 = var2.readLine();
                if (var4.contains("x86")) {
                    var0 = nullToStr("i686");
                } else {
                    var0 = nullToStr(System.getProperty("os.arch"));
                }
            } catch (Throwable var18) {
                var0 = nullToStr(System.getProperty("os.arch"));
                var18.printStackTrace();
            } finally {
                try {
                    if (var2 != null) {
                        var2.close();
                    }
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
                try {
                    if (var1 != null) {
                        var1.close();
                    }
                } catch (IOException var16) {
                    var16.printStackTrace();
                }

            }

            return var0;
        }
    }

    private static String nullToStr(String var0) {
        return var0 == null ? "" : var0;
    }

    public static String getAndroidId(Context var0) {
        if (!TextUtils.isEmpty(androidId)) {
            return androidId;
        } else {
            try {
                androidId = Settings.Secure.getString(var0.getContentResolver(), "android_id");
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            return androidId;
        }
    }


    //获取本应用的签名
    public static String getApkSignatures(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        Iterator<PackageInfo> inter = apps.iterator();
        while (inter.hasNext()) {
            PackageInfo packageinfo = inter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                return packageinfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    //获取应用的签名信息
    public static String getApkSignatures(Context context, File file) {
        String var2 = getApkSignatures(context, file, false);
        if (var2 == null) {
            var2 = getApkSignatures(file);
        }
        if (var2 == null) {
            var2 = getApkSignatures(context, file, true);
        }
        return var2;
    }

    private static String getApkSignatures(Context context, File file, boolean isForce) {
        String var3 = null;
        PackageInfo packageInfo = null;
        if (isForce) {
            packageInfo = parsePackage(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        } else {
            packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
        }
        Signature var5 = null;
        if (packageInfo != null) {
            if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                var5 = packageInfo.signatures[0];
                parseSignature(var5.toByteArray());
            } else {
                Log.w("dds", "[getSignatureFromApk] pkgInfo is not null BUT signatures is null!");
            }
        }
        if (var5 != null) {
            var3 = var5.toCharsString();
        }

        return var3;
    }

    private static void parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            Log.d("dds", "signName:" + cert.getSigAlgName());
            Log.d("dds", "pubKey:" + pubKey);
            Log.d("dds", "signNumber:" + signNumber);
            Log.d("dds", "subjectDN:" + cert.getSubjectDN().toString());
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    private static String getApkSignatures(File file) {
        String var1 = null;
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry manifest = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] bytes = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, manifest, bytes);
            if (certs != null) {
                var1 = getSignFromManifest(certs[0].getEncoded());
            }
            Enumeration entries = jarFile.entries();
            String var7 = null;
            Certificate[] certificates = null;

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                String var10 = jarEntry.getName();
                if (var10 != null) {
                    certificates = loadCertificates(jarFile, jarEntry, bytes);
                    var7 = null;
                    if (certificates != null) {
                        var7 = getSignFromManifest(certificates[0].getEncoded());
                    }

                    if (var7 == null) {
                        if (!var10.startsWith("META-INF/")) {
                            var1 = null;
                            break;
                        }
                    } else {
                        boolean var11 = var7.equals(var1);
                        if (!var11) {
                            var1 = null;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            var1 = null;
            e.printStackTrace();
        }

        return var1;
    }

    //如果apk未安装时，当我们需要获取APK包的相关信息时，可以直接使用这个类
    private static PackageInfo parsePackage(String archiveFilePath, int flag) {
        try {
            Class packageParser = Class.forName("android.content.pm.PackageParser");
            Class[] classes = packageParser.getDeclaredClasses();
            Class var4 = null;
            Class[] var5 = classes;
            int var6 = classes.length;
            for (int var7 = 0; var7 < var6; ++var7) {
                Class var8 = var5[var7];
                if (0 == var8.getName().compareTo("android.content.pm.PackageParser$Package")) {
                    var4 = var8;
                    break;
                }
            }
            Constructor var14 = packageParser.getConstructor(new Class[]{String.class});
            Method method_parsePackage = packageParser.getDeclaredMethod("parsePackage",
                    new Class[]{File.class, String.class, DisplayMetrics.class, Integer.TYPE});
            Method method_collectCertificates = packageParser.getDeclaredMethod("collectCertificates",
                    new Class[]{var4, Integer.TYPE});
            Method method_generatePackageInfo = packageParser.getDeclaredMethod("generatePackageInfo",
                    new Class[]{var4, int[].class, Integer.TYPE, Long.TYPE, Long.TYPE});
            var14.setAccessible(true);
            method_parsePackage.setAccessible(true);
            method_collectCertificates.setAccessible(true);
            method_generatePackageInfo.setAccessible(true);
            Object var9 = var14.newInstance(new Object[]{archiveFilePath});
            DisplayMetrics var10 = new DisplayMetrics();
            var10.setToDefaults();
            File var11 = new File(archiveFilePath);
            Object var12 = method_parsePackage.invoke(var9, new Object[]{var11, archiveFilePath, var10, Integer.valueOf(0)});
            if (var12 == null) {
                return null;
            } else {
                if ((flag & 64) != 0) {
                    method_collectCertificates.invoke(var9, new Object[]{var12, Integer.valueOf(0)});
                }
                return (PackageInfo) method_generatePackageInfo.invoke(null, new Object[]{var12, null, Integer.valueOf(flag), Integer.valueOf(0), Integer.valueOf(0)});
            }
        } catch (Exception var13) {
            var13.printStackTrace();
            return null;
        }
    }

    //loadCertificates
    private static Certificate[] loadCertificates(JarFile var0, JarEntry var1, byte[] var2) {
        InputStream var3;
        try {
            var3 = var0.getInputStream(var1);
            while (var3.read(var2, 0, var2.length) != -1) {
            }
            var3.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return var1 != null ? var1.getCertificates() : null;
    }

    private static String getSignFromManifest(byte[] bytes) {
        int length = bytes.length;
        int length2 = length * 2;
        char[] chars = new char[length2];

        for (int i = 0; i < length; ++i) {
            byte b = bytes[i];
            int j = b >> 4 & 15;
            chars[i * 2] = (char) (j >= 10 ? 97 + j - 10 : 48 + j);
            j = b & 15;
            chars[i * 2 + 1] = (char) (j >= 10 ? 97 + j - 10 : 48 + j);
        }

        return new String(chars);
    }


    // 获取应用列表
    public static List<PackageInfo> getAllApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNING_CERTIFICATES);
        } else {
            installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
        }
        return installedPackages;
    }

    public static byte[] getSignMd5(Context context, String pkgName) {
        Signature[] rawSignature = getRawSignature(context, pkgName);
        byte[] md5 = null;
        if (rawSignature != null) {
            md5 = getMD5(rawSignature[0].toByteArray());
        }
        return md5;
    }

    public static PackageInfo getPkgInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        return pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES | PackageManager.GET_SIGNATURES);
    }

    private static Signature[] getRawSignature(Context context, String pkg) {
        if ((pkg == null) || (pkg.length() == 0)) {
            LogB.e(TAG, "获取签名失败，包名为 null");
            return null;
        }
        PackageManager localPackageManager = context.getPackageManager();
        PackageInfo localPackageInfo;
        try {
            localPackageInfo = localPackageManager.getPackageInfo(pkg, PackageManager.GET_SIGNATURES);
            if (localPackageInfo == null) {
                LogB.e(TAG, "信息为 null, 包名 = " + pkg);
                return null;
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            LogB.e(TAG, "包名没有找到...");
            return null;
        }


        return localPackageInfo.signatures;
    }

    private static byte[] getMD5(byte[] data) {
        byte[] keyMD5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            keyMD5 = md.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyMD5;
    }

}

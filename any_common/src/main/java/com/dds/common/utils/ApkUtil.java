package com.dds.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 解析apk
 */
public class ApkUtil {


    /**
     * 获取应用的签名信息
     */
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

    /**
     * 如果apk未安装时，当我们需要获取APK包的相关信息时，可以直接使用这个类
     */
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

    /** @param v version code as of {@link android.os.Build.VERSION#SDK_INT} */
    public static String getAndroidVersionNumber(final int v) {
        switch (v) {
            case 29: return "Q";
            case 28: return "9.0";
            case 27: return "8.1";
            case 26: return "8.0";
            case 25: return "7.1";
            case 24: return "7.0";
            case 23: return "6.0";
            case 22: return "5.1";
            case 21: return "5.0";
            case 20: return "4.4W";
            case 19: return "4.4";
            case 18: return "4.3";
            case 17: return "4.2";
            case 16: return "4.1";
            case 15:
            case 14: return "4.0";
            case 13: return "3.2";
            case 12: return "3.1";
            case 11: return "3.0";
            default:
                if (v > 28) return "9+";
                if (v > 4) return "2.x";
                return "1.x";
        }
    }


}

package com.dds.cipher.util;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class ByteUtil {

    // hex2Bytes
    public static byte[] hexStr2Bytes(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;

        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    // bytes2HexStr
    public static String byte2HexStr(byte[] b) {
        if (b == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * b.length);
        for (int i = 0; ; i++) {
            if (i >= b.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & b[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }
}

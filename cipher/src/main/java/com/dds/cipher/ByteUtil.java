package com.dds.cipher;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class ByteUtil {

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        if (hexString.length() % 2 != 0) {
            hexString = hexString.substring(0, hexString.length() - 1)
                    + "0"
                    + hexString.substring(hexString.length() - 1
            );
        }

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String byte2HexStr(byte[] b, int len) {
        String stmt;
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < len; n++) {
            stmt = Integer.toHexString(b[n] & 0xFF);
            if (stmt.length() == 1) {
                sb.append("0").append(stmt);
            } else {
                sb.append(stmt);
            }
        }
        return sb.toString().toUpperCase().trim();
    }
}

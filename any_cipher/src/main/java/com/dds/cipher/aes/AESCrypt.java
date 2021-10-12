package com.dds.cipher.aes;

import android.util.Log;

import com.dds.cipher.base64.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * AES Encrypt/Decrypt
 *
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class AESCrypt {
    private static final String TAG = "AESCrypt";
    private static final String CHARSET = "UTF-8";

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    //toggleable log option (please turn off in live!)
    public static boolean DEBUG_LOG_ENABLED = true;


    /**
     * Encrypt and encode message using 256-bit AES with key generated from password.
     *
     * @param password   used to generated key
     * @param message    the thing you want to encrypt assumed String UTF-8
     * @param needDigest true:Generates hash of the password  false:no deal
     * @param algorithm  SHA1/SHA-256/MD5
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    public static String encrypt(final String password, String message, boolean needDigest, String algorithm, String aes_mode, final byte[] iv) throws GeneralSecurityException {
        try {
            if (aes_mode.contains("NoPadding")) {
                //不足16的倍数补空格
                if (message.getBytes(CHARSET).length % 16 != 0) {
                    int tem = message.getBytes(CHARSET).length % 16;
                    StringBuilder messageBuilder = new StringBuilder(message);
                    for (int i = 0; i < 16 - tem; i++) {
                        messageBuilder.append(" ");
                    }
                    message = messageBuilder.toString();
                }
            }

            log("input message", message);
            final SecretKeySpec key = generateKey(password, needDigest, algorithm);

            byte[] cipherText = encrypt(key, message.getBytes(CHARSET), iv, aes_mode);

            String encoded = new String(Base64.encode(cipherText), CHARSET);

            log("base64Enc result", encoded);
            return encoded;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "UnsupportedEncodingException ", e);
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit  {@link #generateKey(String, boolean, String)}
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] message, byte[] iv, String aes_mode) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            if (iv == null) {
                iv = ivBytes;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }
        byte[] cipherText = cipher.doFinal(message);
        log("AES enc result", cipherText);
        return cipherText;
    }

    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password  used to generated key
     * @param base64Enc the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decrypt(final String password, String base64Enc, boolean needDigest, String algorithm, String aes_mode, final byte[] iv) throws GeneralSecurityException {
        try {
            log("input message", base64Enc);

            final SecretKeySpec key = generateKey(password, needDigest, algorithm);

            byte[] decodedCipherText = Base64.decode(base64Enc.getBytes(CHARSET));
            log("base64Dec", decodedCipherText);

            byte[] decryptedBytes = decrypt(key, decodedCipherText, iv, aes_mode);

            String message = new String(decryptedBytes, CHARSET);

            log("AES dec result", message);


            return message;
        } catch (UnsupportedEncodingException e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "UnsupportedEncodingException ", e);

            throw new GeneralSecurityException(e);
        }
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKeySpec key, final byte[] decodedCipherText, byte[] iv, String aes_mode) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            if (iv == null) {
                iv = ivBytes;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        return cipher.doFinal(decodedCipherText);
    }

    /**
     * Encrypt file return target file path
     *
     * @param password   used to generated key
     * @param srcPath    source file path
     * @param targetDir  target file dir
     * @param needDigest true:Generates hash of the password  false:no deal
     * @param algorithm  SHA1/SHA-256/MD5
     * @param aes_mode   CBC/ECB
     * @param iv         Initiation Vector
     * @return target file path
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static String encryptFile(final String password, String srcPath, String targetDir, boolean needDigest, String algorithm, String aes_mode, final byte[] iv) throws Exception {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            return null;
        }
        try {
            final SecretKeySpec key = generateKey(password, needDigest, algorithm);
            String fileName = srcFile.getName();
            File targetFile = new File(targetDir, fileName);
            encryptFile(key, srcFile, targetFile, iv, aes_mode);
            if (targetFile.exists()) {
                return targetFile.getAbsolutePath();
            }
        } catch (Exception e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "encFile Exception ", e);
            throw new Exception(e);
        }
        return null;

    }

    /**
     * Encrypt file
     *
     * @param key      AES key typically 128, 192 or 256 bit
     * @param source   source file
     * @param target   target file
     * @param iv       Initiation Vector
     * @param aes_mode CBC/ECB
     * @throws IOException                        IOException
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException
     */
    private static void encryptFile(final SecretKeySpec key, File source, File target, byte[] iv, String aes_mode) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        FileInputStream fis = new FileInputStream(source);
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            if (iv == null) {
                iv = ivBytes;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }
        OutputStream out = new FileOutputStream(target);
        CipherInputStream cin = new CipherInputStream(fis, cipher);
        byte[] buffer = new byte[1024];
        int i;
        while ((i = cin.read(buffer)) != -1) {
            out.write(buffer, 0, i);
        }
        out.flush();
        out.close();
        cin.close();
    }

    /**
     * Decrypt file return target file path
     *
     * @param password   used to generated key
     * @param srcPath    source file path
     * @param targetDir  target file dir
     * @param needDigest true:Generates hash of the password  false:no deal
     * @param algorithm  SHA1/SHA-256/MD5
     * @param aes_mode   CBC/ECB
     * @param iv         Initiation Vector
     * @return target file path
     * @throws Exception if something goes wrong during encryption
     */
    public static String decryptFile(final String password, String srcPath, String targetDir, boolean needDigest, String algorithm, String aes_mode, final byte[] iv) throws Exception {
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) {
            return null;
        }
        try {
            final SecretKeySpec key = generateKey(password, needDigest, algorithm);
            String fileName = srcFile.getName();
            File targetFile = new File(targetDir, fileName);
            decryptFile(key, srcFile, targetFile, iv, aes_mode);
            if (targetFile.exists()) {
                return targetFile.getAbsolutePath();
            }
        } catch (Exception e) {
            if (DEBUG_LOG_ENABLED)
                Log.e(TAG, "encFile Exception ", e);
            throw new Exception(e);
        }
        return null;

    }

    /**
     * Decrypt file
     *
     * @param key      AES key typically 128, 192 or 256 bit
     * @param source   source file
     * @param target   target file
     * @param iv       Initiation Vector
     * @param aes_mode CBC/ECB
     * @throws IOException                        IOException
     * @throws NoSuchPaddingException             NoSuchPaddingException
     * @throws NoSuchAlgorithmException           NoSuchAlgorithmException
     * @throws InvalidKeyException                InvalidKeyException
     * @throws InvalidAlgorithmParameterException InvalidAlgorithmParameterException
     */
    private static void decryptFile(final SecretKeySpec key, File source, File target, byte[] iv, String aes_mode) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        FileInputStream fis = new FileInputStream(source);
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            if (iv == null) {
                iv = ivBytes;
            }
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        OutputStream out = new FileOutputStream(target);
        CipherInputStream cin = new CipherInputStream(fis, cipher);
        byte[] buffer = new byte[1024];
        int i;
        while ((i = cin.read(buffer)) != -1) {
            out.write(buffer, 0, i);
        }
        out.flush();
        out.close();
        cin.close();
    }

    /**
     * Generates hash of the password which is used as key
     *
     * @param password   password
     * @param needDigest true:Generates hash of the password  false:no deal
     * @param algorithm  SHA1/SHA-256/MD5
     * @return SecretKeySpec
     */
    public static SecretKeySpec generateKey(final String password, boolean needDigest, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes;
        if (needDigest) {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytesPwd = password.getBytes(CHARSET);
            digest.update(bytesPwd, 0, bytesPwd.length);
            bytes = digest.digest();
            log("digest key " + algorithm, bytes);
        } else {
            bytes = password.getBytes(CHARSET);
            log("algorithm:" + algorithm + ",key ", bytes);
        }
        return new SecretKeySpec(bytes, "AES");
    }

    private static void log(String what, byte[] bytes) {
        if (DEBUG_LOG_ENABLED)
            Log.d(TAG, what + "[" + bytes.length + "] [" + bytesToHex(bytes) + "]");
    }

    private static void log(String what, String value) {
        if (DEBUG_LOG_ENABLED)
            Log.d(TAG, what + "[" + value.length() + "] [" + value + "]");
    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}

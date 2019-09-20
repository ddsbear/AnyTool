package com.dds.cipher.aes;

import android.util.Log;

import com.dds.cipher.base64.Base64;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dds on 2019/9/9.
 * android_shuai@163.com
 */
public class AESCrypt {
    private static final String TAG = "dds_test";
    private static final String CHARSET = "UTF-8";

    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00};

    //togglable log option (please turn off in live!)
    public static boolean DEBUG_LOG_ENABLED = true;


    /**
     * Generates hash of the password which is used as key
     *
     * @param password   password
     * @param needDigest true:Generates hash of the password  false:no deal
     * @param algorithm  SHA1/SHA-256/MD5
     * @return SecretKeySpec
     */
    private static SecretKeySpec generateKey(final String password, boolean needDigest, String algorithm)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes;
        if (needDigest) {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytesPwd = password.getBytes(CHARSET);
            digest.update(bytesPwd, 0, bytesPwd.length);
            bytes = digest.digest();
            log(algorithm + " key ", bytes);
        } else {
            bytes = password.getBytes(CHARSET);
            log(algorithm + " key ", bytes);
        }


        return new SecretKeySpec(bytes, "AES");
    }


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
    public static String encrypt(final String password, String message, boolean needDigest,
                                 String algorithm, String aes_mode, final byte[] iv)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(password, needDigest, algorithm);
            if(aes_mode.contains("NoPadding")){
                //不足16的倍数补空格
                if (message.getBytes().length % 16 != 0) {
                    int tem = message.getBytes().length % 16;
                    StringBuilder messageBuilder = new StringBuilder(message);
                    for (int i = 0; i < 16 - tem; i++) {
                        messageBuilder.append(" ");
                    }
                    message = messageBuilder.toString();
                }
            }

            log("input message", message);

            byte[] cipherText = encrypt(key, message.getBytes(CHARSET), iv, aes_mode);

            String encoded = new String(Base64.encode(cipherText));

            log("Base64 encode", encoded);
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
     * @param key     AES key typically 128, 192 or 256 bit
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] message, final byte[] iv, String aes_mode)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        }
        byte[] cipherText = cipher.doFinal(message);
        log("cipherText hex", cipherText);
        return cipherText;
    }


    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password                used to generated key
     * @param base64EncodedCipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decrypt(final String password, String base64EncodedCipherText, boolean needDigest,
                                 String algorithm, String aes_mode, final byte[] iv)
            throws GeneralSecurityException {

        try {
            final SecretKeySpec key = generateKey(password, needDigest, algorithm);

            log("base64EncodedCipherText", base64EncodedCipherText);
            byte[] decodedCipherText = Base64.decode(base64EncodedCipherText.getBytes());
            log("decodedCipherText", decodedCipherText);

            byte[] decryptedBytes = decrypt(key, decodedCipherText, ivBytes, aes_mode);

            String message = new String(decryptedBytes);

            log("output message", message);


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
    public static byte[] decrypt(final SecretKeySpec key, final byte[] decodedCipherText, final byte[] iv, String aes_mode)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(aes_mode);
        if (aes_mode.contains("CBC")) {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        log("decrypted text hex", decryptedBytes);

        return decryptedBytes;
    }


    private static void log(String what, byte[] bytes) {
        if (DEBUG_LOG_ENABLED)
            Log.d(TAG, what + "[" + bytes.length + "] [" + bytesToHex(bytes) + "]");
    }

    private static void log(String what, String value) {
        if (DEBUG_LOG_ENABLED)
            Log.d(TAG, what + "[" + value.length() + "] [" + value + "]");
    }

    public static String bytesToHex(byte[] bytes) {
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

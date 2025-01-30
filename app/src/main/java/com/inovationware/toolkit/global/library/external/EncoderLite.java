package com.inovationware.toolkit.global.library.external;

import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.UUID;

public final class EncoderLite {

    private static EncoderLite instance;

    public static EncoderLite getInstance() {
        if (instance == null) instance = new EncoderLite();
        return instance;
    }

    private EncoderLite() {
    }

    public String encrypt(String plainText, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        byte[] encryptedData = cipher.doFinal(plainText.getBytes());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(encryptedData);
        }
        return plainText;
    }

    public String decrypt(String encryptedText, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        byte[] decryptedData = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedData);
        }
        return encryptedText;
    }

    public byte[] generateKey() throws UnsupportedEncodingException {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).getBytes("UTF-8");
    }

    public String toUtf8String(byte[] bits) throws UnsupportedEncodingException {
        return new String(bits, "UTF-8");
    }

    public byte[] fromUTF8String(String s) throws UnsupportedEncodingException {
        return s.getBytes("UTF-8");
    }
}
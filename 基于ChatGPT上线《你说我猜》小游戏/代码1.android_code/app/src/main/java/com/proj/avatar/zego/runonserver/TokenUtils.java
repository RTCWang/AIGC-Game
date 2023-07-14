package com.proj.avatar.zego.runonserver;


import android.util.Log;

import com.proj.avatar.utils.Base64;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TokenUtils {
    private final static String TAG = "TokenServerAssistant";
    static final private String VERSION_FLAG = "04";
    static final private int IV_LENGTH = 16;
    static final private String TRANSFORMATION = "AES/CBC/PKCS5Padding";


    private TokenUtils() {
    }

    static public String generateToken04(TokenEntity tokenEntity) {


        byte[] ivBytes = new byte[IV_LENGTH];

        new Random().nextBytes(ivBytes);

        try {
            String content = tokenEntity.toString();
            byte[] contentBytes = encrypt(content.getBytes("UTF-8"), KeyCenter.SERVER_SECRET.getBytes(), ivBytes);

            ByteBuffer buffer = ByteBuffer.wrap(new byte[contentBytes.length + IV_LENGTH + 12]);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putLong(tokenEntity.expire);     // 8 bytes
            packBytes(ivBytes, buffer);      // IV_LENGTH + 2 bytes
            packBytes(contentBytes, buffer); // contentBytes.length + 2 bytes

            return VERSION_FLAG + Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            Log.e(TAG, "generate token failed: " + e);
        }

        return null;
    }

    public static String decryptToken(String token) {
        String secretKey = KeyCenter.SERVER_SECRET;
        String noVersionToken = token.substring(2);

        byte[] tokenBytes = Base64.getDecoder().decode(noVersionToken.getBytes());
        ByteBuffer buffer = ByteBuffer.wrap(tokenBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);
        long expiredTime = buffer.getLong();
        System.out.println("expiredTime: " + expiredTime);
        int IVLength = buffer.getShort();
        byte[] ivBytes = new byte[IVLength];
        buffer.get(ivBytes);
        int contentLength = buffer.getShort();
        byte[] contentBytes = new byte[contentLength];
        buffer.get(contentBytes);

        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] rawBytes = cipher.doFinal(contentBytes);
            return new String(rawBytes);
        } catch (Exception e) {
            Log.e(TAG, "decrypt failed: " + e);
        }
        return null;
    }

    private static byte[] encrypt(byte[] content, byte[] secretKey, byte[] ivBytes)
            throws Exception {
        if (secretKey == null || secretKey.length != 32) {
            throw new IllegalArgumentException("secret key's length must be 32 bytes");
        }

        if (ivBytes == null || ivBytes.length != 16) {
            throw new IllegalArgumentException("ivBytes's length must be 16 bytes");
        }

        if (content == null) {
            content = new byte[]{};
        }
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        return cipher.doFinal(content);
    }

    private static void packBytes(byte[] buffer, ByteBuffer target) {
        target.putShort((short) buffer.length);
        target.put(buffer);
    }


}
package com.encryptmsg.msgdog.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacShaUtils {

    public static String getAssociatedData(String salt, String secretKey) {
        String associatedData = "";
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(salt.getBytes(StandardCharsets.UTF_8));
            associatedData = Base64.getEncoder().encodeToString(hmacSha256);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return associatedData;
    }

    public static String encryptPath(String path, String secretKey) {
        String encryptPath = "";
        byte[] hmacSha256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(path.getBytes(StandardCharsets.UTF_8));
            encryptPath = Base64.getEncoder().encodeToString(hmacSha256);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return encryptPath;
    }

    public static String encryptSecondPwd(String secondPassword, String secretKey) {
        String encryptPath = "";
        byte[] hmacSha512 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHAHmacSHA512");
            mac.init(secretKeySpec);
            hmacSha512 = mac.doFinal(secondPassword.getBytes(StandardCharsets.UTF_8));
            encryptPath = Base64.getEncoder().encodeToString(hmacSha512);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate hmac-sha256", e);
        }
        return encryptPath;
    }
}

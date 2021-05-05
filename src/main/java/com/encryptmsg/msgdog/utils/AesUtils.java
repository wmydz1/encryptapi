package com.encryptmsg.msgdog.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

import java.util.Base64;

public class AesUtils {

    public static String encrypt(String plainText, String keyBase64String) {
        byte[] key = Base64.getDecoder().decode(keyBase64String);
        // 构建
        AES aes = SecureUtil.aes(key);
        // 加密为16进制表示
        String encryptHex = aes.encryptHex(plainText);
        return encryptHex;
    }

    public static String decrypt(String encryptHex, String keyBase64String) {
        byte[] key = Base64.getDecoder().decode(keyBase64String);
        // 构建
        AES aes = SecureUtil.aes(key);
        // 解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        return decryptStr;
    }
}

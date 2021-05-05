package com.encryptmsg.msgdog.utils;

import com.encryptmsg.msgdog.bo.CiphertextBase64Bo;
import com.google.crypto.tink.*;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.daead.AesSivKeyManager;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TinkUtil {

    public static CiphertextBase64Bo encrypt(String plainText, String associatedData) {
        CiphertextBase64Bo ciphertextBase64Bo = null;
        try {
            TinkConfig.register();
            KeysetHandle keysetHandle = KeysetHandle.generateNew(
                    AesSivKeyManager.aes256SivTemplate());
            ByteArrayOutputStream stream
                    = new ByteArrayOutputStream();
            CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withOutputStream(stream));
            DeterministicAead daead =
                    keysetHandle.getPrimitive(DeterministicAead.class);
            byte[] ciphertext = daead.encryptDeterministically(plainText.getBytes(StandardCharsets.UTF_8), associatedData.getBytes(StandardCharsets.UTF_8));
            String ciphertextBase64String = Base64.getEncoder().encodeToString(ciphertext);
            String finalString
                    = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            String keyStoreBase64 = Base64.getEncoder().encodeToString(finalString.getBytes(StandardCharsets.UTF_8));
            ciphertextBase64Bo = new CiphertextBase64Bo();
            ciphertextBase64Bo.setCipherText(ciphertextBase64String);
            ciphertextBase64Bo.setKeyStoreText(keyStoreBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertextBase64Bo;
    }

    public static String decrypt(String cipherText, String keyStoreBase64, String associatedData) {
        String decryptString = null;
        try {
            TinkConfig.register();
            byte[] base64decodedBytes = Base64.getDecoder().decode(cipherText);
            KeysetHandle keysetHandle = CleartextKeysetHandle.read(
                    JsonKeysetReader.withBytes(Base64.getDecoder().decode(keyStoreBase64)));
            DeterministicAead daead =
                    keysetHandle.getPrimitive(DeterministicAead.class);
            byte[] decrypted = daead.decryptDeterministically(base64decodedBytes, associatedData.getBytes(StandardCharsets.UTF_8));
            decryptString = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptString;
    }


}

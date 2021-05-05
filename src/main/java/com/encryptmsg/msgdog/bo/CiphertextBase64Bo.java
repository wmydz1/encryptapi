package com.encryptmsg.msgdog.bo;

public class CiphertextBase64Bo {

    private String cipherText;
    private String keyStoreText;

    public CiphertextBase64Bo() {
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getKeyStoreText() {
        return keyStoreText;
    }

    public void setKeyStoreText(String keyStoreText) {
        this.keyStoreText = keyStoreText;
    }

    @Override
    public String toString() {
        return "CiphertextBase64Bo{" +
                "cipherText='" + cipherText + '\'' +
                ", keyStoreText='" + keyStoreText + '\'' +
                '}';
    }
}

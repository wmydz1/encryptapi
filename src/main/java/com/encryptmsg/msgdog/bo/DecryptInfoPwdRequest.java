package com.encryptmsg.msgdog.bo;

public class DecryptInfoPwdRequest {

    private String path;
    private String secondPassword;
    private String lang;

    public DecryptInfoPwdRequest() {
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }
}

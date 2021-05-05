package com.encryptmsg.msgdog.bo;

public class DecryptInfoRequest {

    private String path;
    private String lang;

    public DecryptInfoRequest() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}

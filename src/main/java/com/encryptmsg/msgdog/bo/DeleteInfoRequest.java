package com.encryptmsg.msgdog.bo;

public class DeleteInfoRequest {

    private String deleteToken;
    private String path;

    public DeleteInfoRequest() {
    }

    public String getDeleteToken() {
        return deleteToken;
    }

    public void setDeleteToken(String deleteToken) {
        this.deleteToken = deleteToken;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

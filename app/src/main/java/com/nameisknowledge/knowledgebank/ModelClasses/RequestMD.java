package com.nameisknowledge.knowledgebank.ModelClasses;

public class RequestMD {
    private String Uid,email;

    public RequestMD(String uid, String email) {
        Uid = uid;
        this.email = email;
    }

    public RequestMD() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

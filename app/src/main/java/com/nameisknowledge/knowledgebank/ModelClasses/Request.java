package com.nameisknowledge.knowledgebank.ModelClasses;

public class Request {
    private String Uid,email;

    public Request(String uid, String email) {
        Uid = uid;
        this.email = email;
    }

    public Request() {
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

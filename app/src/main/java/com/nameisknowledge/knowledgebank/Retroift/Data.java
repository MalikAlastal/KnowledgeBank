package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class Data implements Serializable {
    private String senderName,senderId;

    public Data(String senderName,String senderId) {
        this.senderName = senderName;
        this.senderId = senderId;
    }

    public Data() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class Data implements Serializable {
    private String senderName,senderId,mode;

    public Data(String senderName,String senderId,String mode) {
        this.senderName = senderName;
        this.mode = mode;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}

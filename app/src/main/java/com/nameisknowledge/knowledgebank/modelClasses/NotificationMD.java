package com.nameisknowledge.knowledgebank.modelClasses;

public class NotificationMD {
    private String targetToken,senderName,senderId,mode;

    public NotificationMD(String targetToken,String senderName,String senderId,String mode) {
        this.targetToken = targetToken;
        this.senderId = senderId;
        this.mode = mode;
        this.senderName = senderName;
    }

    public NotificationMD() {
    }

    public String getTargetToken() {
        return targetToken;
    }

    public void setTargetToken(String targetToken) {
        this.targetToken = targetToken;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

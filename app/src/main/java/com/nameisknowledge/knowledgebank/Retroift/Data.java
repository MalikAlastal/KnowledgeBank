package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class Data implements Serializable {
    private String senderId;

    public Data(String senderId) {
        this.senderId = senderId;
    }

    public Data() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

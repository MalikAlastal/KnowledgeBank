package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class Data implements Serializable {
    private String senderName;

    public Data(String senderName) {
        this.senderName = senderName;
    }

    public Data() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}

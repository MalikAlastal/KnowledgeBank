package com.nameisknowledge.knowledgebank.Retroift;

import java.io.Serializable;

public class NotificationData implements Serializable {
    private String title,body;

    public NotificationData(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public NotificationData() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}

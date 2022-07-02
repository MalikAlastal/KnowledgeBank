package com.nameisknowledge.knowledgebank.retroift;

import java.io.Serializable;

public class NotificationData implements Serializable {
    private String title,body,click_action;

    public NotificationData(String title, String body,String click_action) {
        this.title = title;
        this.click_action = click_action;
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

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }
}

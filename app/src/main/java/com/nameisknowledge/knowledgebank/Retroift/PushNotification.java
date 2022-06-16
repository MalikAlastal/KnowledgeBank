package com.nameisknowledge.knowledgebank.Retroift;

public class PushNotification {
    private NotificationData notification;
    private String to;
    private Data data;

    public PushNotification(NotificationData notificationData, String to,Data data) {
        this.notification = notificationData;
        this.data = data;
        this.to = to;
    }

    public PushNotification() {
    }

    public NotificationData getNotification() {
        return notification;
    }

    public void setNotification(NotificationData notificationData) {
        this.notification = notificationData;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}

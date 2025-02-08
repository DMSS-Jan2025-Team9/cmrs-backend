package com.example.notificationservice.model;

public class Notification {
    
    private String notificationName;
    private String notificationDescription;

    public Notification(String notificationName, String notificationDescription) {
        this.notificationName = notificationName;
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }
}

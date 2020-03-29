package com.example.easyride.ui;


/**
 * Notification Model.
 * @author T22
 * @version 1.0
 */

public class NotificationModel {
    private String notificationMessage, senderUserEmail;

    /**
     * Model Constructor.
     * @param notificationMessage
     * @param senderUserEmail
     */
    public NotificationModel(String notificationMessage, String senderUserEmail) {
        this.notificationMessage = notificationMessage;
        this.senderUserEmail = senderUserEmail;

    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getSenderUserEmail() {
        return senderUserEmail;
    }

    public void setSenderUserEmail(String senderUserEmail) {
        this.senderUserEmail = senderUserEmail;
    }
}


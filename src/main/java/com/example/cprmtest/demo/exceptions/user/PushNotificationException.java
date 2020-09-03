package com.example.cprmtest.demo.exceptions.user;

public class PushNotificationException extends RuntimeException{
    public PushNotificationException() {
        super("Sorry! Could not push notification");
    }

    public PushNotificationException(String msg) {
        super(msg);
    }
}

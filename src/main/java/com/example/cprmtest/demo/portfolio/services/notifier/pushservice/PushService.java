package com.example.cprmtest.demo.portfolio.services.notifier.pushservice;

import com.example.cprmtest.demo.model.dto.CustomerNotification;

//to support multiple types of push notifications in the future
public abstract class PushService<T> {
    protected String location;
    public abstract void initializeConnection(String location);
    public abstract boolean sendAndCheck(CustomerNotification<T> objectToSend);
}

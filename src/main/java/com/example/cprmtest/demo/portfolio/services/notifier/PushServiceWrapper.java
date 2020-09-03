package com.example.cprmtest.demo.portfolio.services.notifier;

import com.example.cprmtest.demo.model.dto.CustomerNotification;
import com.example.cprmtest.demo.portfolio.services.notifier.pushservice.HTTPPushService;
import org.springframework.stereotype.Component;

//autowirable wrapper created for easier integration testing + simpler api
@Component
public class PushServiceWrapper<T> {
    public boolean sendAndCheck(String location, CustomerNotification<T> notification) {
        HTTPPushService<T> pushService = new HTTPPushService<>(); //need to have separate pushservice instances per url
        pushService.initializeConnection(location);
        return pushService.sendAndCheck(notification);
    }
}

package com.example.cprmtest.demo.model.dto;

//sent to hooks that customer uses to subscribe to nav changes
public class CustomerNotification<T> {
    private Long customerId;
    private Long timestamp;
    private T payload;

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public CustomerNotification(Long customerId, Long timestamp, T payload) {
        this.customerId = customerId;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

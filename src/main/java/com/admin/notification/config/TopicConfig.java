package com.admin.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "tc")
public class TopicConfig {
    @Value("${app.kafka.topics.NOTIFICATION_RECEIVED_REQUEST}")
    public String NOTIFICATION_RECEIVED_REQUEST;

    @Value("${app.kafka.topics.NOTIFICATION_STORED_REQUEST}")
    public String NOTIFICATION_STORED_REQUEST;

    @Value("${app.kafka.listeners.NOTIFICATION_RECEIVED_REQUEST_LISTENER}")
    public String NOTIFICATION_RECEIVED_REQUEST_LISTENER;


}

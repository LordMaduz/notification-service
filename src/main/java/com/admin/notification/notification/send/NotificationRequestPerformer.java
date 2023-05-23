package com.admin.notification.notification.send;

import com.admin.notification.vo.NotificationEvent;
import io.cloudevents.kafka.PartitionKeyExtensionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRequestPerformer {

    private final NotificationRequestPublisher requestPublisher;

    public void publish(NotificationEvent createEvent) {
        requestPublisher.publish(UUID.randomUUID().toString().substring(0, 10), createEvent);
    }
}

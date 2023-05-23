package com.admin.notification.notification.send;

import com.admin.notification.vo.NotificationEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@Service
public interface RequestService {
    Mono<Object> handleNotificationRequest(@RequestBody NotificationEvent createEvent);
}

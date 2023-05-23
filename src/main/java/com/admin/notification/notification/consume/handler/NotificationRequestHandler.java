package com.admin.notification.notification.consume.handler;

import com.admin.notification.service.NotificationSenderService;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRequestHandler implements RequestHandler {

    private final NotificationSenderService service;

    @Override
    public boolean canHandleRequest(NotificationEvent notificationEvent) {
        return true;
    }

    @Override
    public void handleRequest(NotificationEvent notificationEvent) {
        log.info("Message Processed: {}", notificationEvent);
        log.info("Message Received to class:{} ", this.getClass());
        service.sendNotifications(notificationEvent);
    }
}

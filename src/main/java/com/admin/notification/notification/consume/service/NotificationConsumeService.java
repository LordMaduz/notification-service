package com.admin.notification.notification.consume.service;

import com.admin.notification.notification.consume.handler.RequestHandler;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class NotificationConsumeService {
    private final List<RequestHandler> handlers;

    public void handleRequest(NotificationEvent notificationEvent) {
        Optional<RequestHandler> optionalNotificationHandler = getHandler(notificationEvent);
        optionalNotificationHandler.ifPresent((handler)-> handler.handleRequest(notificationEvent));
    }

    private Optional<RequestHandler> getHandler(NotificationEvent requestVO) {
        return handlers.stream().filter(handler -> handler.canHandleRequest(requestVO)).findFirst();
    }
}

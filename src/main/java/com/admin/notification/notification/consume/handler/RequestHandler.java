package com.admin.notification.notification.consume.handler;

import com.admin.notification.vo.NotificationEvent;

public interface RequestHandler {
    boolean canHandleRequest(NotificationEvent requestVO);

    void handleRequest(NotificationEvent requestVO);
}

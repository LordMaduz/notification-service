package com.admin.notification.notification.send.impl;

import com.admin.notification.notification.send.NotificationRequestPerformer;
import com.admin.notification.notification.send.RequestService;
import com.admin.notification.util.ResponseUtility;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRequestService implements RequestService {

    private final NotificationRequestPerformer requestPerformer;

    private final ResponseUtility responseUtility;

    public Mono<Object> handleNotificationRequest(NotificationEvent event) {
        try {
            requestPerformer.publish(event);
            return Mono.just(responseUtility.successResponse("Successful Operation", "200"));
        } catch (Exception e) {
            return Mono.error(new RuntimeException(e));
        }
    }
}

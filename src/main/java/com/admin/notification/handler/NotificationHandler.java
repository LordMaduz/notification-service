package com.admin.notification.handler;

import com.admin.notification.notification.send.RequestService;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@RequiredArgsConstructor
public class NotificationHandler {

    private final RequestService requestService;

    public Mono<ServerResponse> triggerNotification(ServerRequest serverRequest) {

        Mono<Object> mono = serverRequest.bodyToMono(NotificationEvent.class).flatMap(requestService::handleNotificationRequest);
        return Mono
                .from(mono)
                .flatMap(p -> ServerResponse
                        .status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(p))
                );
    }
}

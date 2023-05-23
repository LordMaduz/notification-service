package com.admin.notification.router;

import com.admin.notification.handler.NotificationHandler;
import com.admin.notification.notification.send.RequestService;
import com.admin.notification.vo.NotificationEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {

    private static final String TRIGGER_NOTIFICATION = "/trigger-notification";


    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = TRIGGER_NOTIFICATION, produces = {
                            MediaType.APPLICATION_JSON_VALUE},
                            beanClass = RequestService.class, method = RequestMethod.POST, beanMethod = "handleNotificationRequest",
                            operation = @Operation(operationId = "handleNotificationRequest", responses = {
                                    @ApiResponse(responseCode = "200", description = "successful Operation",
                                            content = @Content(schema = @Schema(implementation = NotificationEvent.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid Employee details supplied")}
                            ))
            })
    public RouterFunction<ServerResponse> templateRoutes(NotificationHandler notificationHandler) {
        return RouterFunctions
                .route(POST(TRIGGER_NOTIFICATION).and(accept(MediaType.APPLICATION_JSON))
                        , notificationHandler::triggerNotification)
                ;
    }
}

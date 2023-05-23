package com.admin.notification.service;


import com.admin.notification.config.ChannelConfiguration;
import com.admin.notification.model.*;
import com.admin.notification.model.document.Payload;
import com.admin.notification.model.enums.Channel;
import com.admin.notification.repo.EventRepository;
import com.admin.notification.repo.PayloadRepository;
import com.admin.notification.repo.UserRepository;
import com.admin.notification.util.NotificationUtil;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.admin.notification.constant.Constant.CHANNEL_CONFIGURATION;
import static com.admin.notification.constant.Constant.CHANNEL_END_POINT;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationSenderService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PayloadRepository payloadRepository;
    private final NotificationUtil notificationUtil;
    private final ChannelConfiguration channelConfiguration;

    @Async
    public void sendNotifications(NotificationEvent notificationEvent) {
        eventRepository.findById(notificationEvent.getEventId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("No Event found with given eventID {}", notificationEvent.getEventId());
                    return Mono.empty();
                }))
                .subscribe(event -> {
                    userRepository.findById(notificationEvent.getUserId())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.error("No User found with given userId {}", notificationEvent.getUserId());
                                return Mono.empty();
                            }))
                            .subscribe(user -> {
                                Optional<Subscription> userSubscription = user.getSubscriptions().stream().filter(subscription -> subscription.getEventId().equals(event.getId())).findFirst();
                                userSubscription.ifPresentOrElse(subscription -> {
                                    List<Channel> channelList = subscription.getChannels();

                                    Flux.fromIterable(event.getChannelMetaData()).filter(channelMetaData ->
                                            channelList.contains(channelMetaData.getChannelType())
                                    ).subscribe(channel -> {
                                        try {
                                            Optional<Version> versionOptional = channel.getTemplate().getVersions().stream().filter(Version::isActive).findFirst();
                                            versionOptional.ifPresent(version -> {
                                                payloadRepository.findByEventIdAndTemplateIdAndVersionId(event.getId(), channel.getTemplate().getTemplateId(), version.getId())
                                                        .subscribe(payload -> {
                                                            sendCamelNotification(channel, payload, (Map) notificationEvent.getObject());
                                                        });
                                            });
                                        } catch (Exception e) {
                                            log.error("Error: {}", e.getMessage());
                                        }
                                    });
                                }, () -> {
                                    log.error("User Has no Subscriptions for the given eventId {}", notificationEvent.getEventId());
                                });
                            });
                });
    }

    @SuppressWarnings(value = "unchecked")
    public void sendCamelNotification(final ChannelMetaData channel, final Payload payload, Map<String, Object> map) {
        final CamelContext context = new DefaultCamelContext();
        try {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {

                    from("direct:start")
                            .to(channelConfiguration.getChannels().get(channel.getChannelType().name()).get(CHANNEL_END_POINT).toString())
                            .log("Notification sent with content ${in.body}");
                }
            });

            context.start();
            Endpoint endpoint = context.getEndpoint("direct:start");

            Exchange exchange = endpoint.createExchange();
            Message in = exchange.getIn();

            channel.getConfiguration().forEach(in::setHeader);
            ((Map<String, Object>) channelConfiguration.getChannels().get(channel.getChannelType().name()).get(CHANNEL_CONFIGURATION)).forEach((k, v) -> {
                in.setHeader(notificationUtil.formatChannelKey(k), v);
            });

            in.setBody(notificationUtil.toBody(payload.getBody(), payload.getType(), map));

            Producer producer = endpoint.createProducer();
            producer.start();
            producer.process(exchange);
            context.stop();

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

}

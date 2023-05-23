package com.admin.notification.notification.send;

import com.admin.notification.config.TopicConfig;
import com.admin.notification.util.CloudEventUtil;
import com.admin.notification.vo.NotificationEvent;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.kafka.sender.SenderRecord;

@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationRequestPublisher {

    private final TopicConfig topicConfig;

    private final ReactiveKafkaProducerTemplate<String, CloudEvent> kafkaProducerTemplate;

    private final CloudEventUtil<NotificationEvent> cloudEventUtil;

    public void publish(String key, NotificationEvent notificationEvent) {

        final CloudEvent event = cloudEventUtil.pojoCloudEvent(notificationEvent, notificationEvent.getEventId());

        kafkaProducerTemplate.send(SenderRecord.create(new ProducerRecord<>(
                        topicConfig.NOTIFICATION_RECEIVED_REQUEST,
                        key, event), new Object()))
                .doOnError(error -> log.info("unable to send message due to: {}", error.getMessage())).subscribe(record -> {
                    RecordMetadata metadata = record.recordMetadata();
                    log.info("send message with partition: {} offset: {}", metadata.partition(), metadata.offset());
                });
    }
}

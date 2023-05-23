package com.admin.notification.notification.consume;

import com.admin.notification.notification.consume.service.NotificationConsumeService;
import com.admin.notification.util.CloudEventUtil;
import com.admin.notification.vo.NotificationEvent;
import io.cloudevents.CloudEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Log4j2
@Component
@RequiredArgsConstructor
public class NotificationConsumeListener {

    private final ReactiveKafkaConsumerTemplate<String, CloudEvent> reactiveKafkaConsumerTemplate;
    private final NotificationConsumeService notificationConsumeService;
    private final CloudEventUtil<NotificationEvent> cloudEventUtils;
    private final Scheduler SCHEDULER = Schedulers.newBoundedElastic(60, 60, "writerThreads");

    @PostConstruct
    public void initialize() {
        onNotificationRequestReceived();
    }

    private ReceiverOffset processRecord(TopicPartition topicPartition, ReceiverRecord<String, CloudEvent> message) {
        log.info("Processing record {} from partition {} in thread{}",
                message.value(), topicPartition, Thread.currentThread().getName());
        NotificationEvent event = cloudEventUtils.toObject(message.value(), NotificationEvent.class);
        sendNotification(event);
        return message.receiverOffset();
    }


    public void onNotificationRequestReceived() {

        reactiveKafkaConsumerTemplate
                .receive()
                .groupBy(m -> m.receiverOffset().topicPartition())
                .flatMap(partitionFlux ->
                        partitionFlux.publishOn(SCHEDULER)
                                .map(r -> processRecord(partitionFlux.key(), r))
                                .sample(Duration.ofMillis(5000))
                                .concatMap(ReceiverOffset::commit))
                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage())).subscribe();

    }

    public Flux<CloudEvent> onNotificationRequestReceived_() {

        return reactiveKafkaConsumerTemplate
                .receive()

                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(), consumerRecord.value(), consumerRecord.topic(), consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .doOnNext(notificationEvent -> {
                    log.info("successfully consumed {}={}", NotificationEvent.class.getSimpleName(), notificationEvent);
                    sendNotification(new NotificationEvent());
                });
    }

    @Async
    private void sendNotification(NotificationEvent event) {
        notificationConsumeService.handleRequest(event);
    }
}

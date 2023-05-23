/**
 * Copyright 2019 Sven Loesekann
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.admin.notification.notification.stream;


import com.admin.notification.config.KafkaBasicConfig;
import com.admin.notification.config.TopicConfig;
import com.admin.notification.vo.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import javax.annotation.PostConstruct;
import java.util.Properties;

//@Component
@RequiredArgsConstructor
public class KafkaStreamsConfig extends KafkaBasicConfig {
    private final StreamsBuilder builder = new StreamsBuilder();

    private final TopicConfig topicConfig;

    //@PostConstruct
    public void startStreamBuilder() {

        final JsonDeserializer<NotificationEvent> deserializer = new JsonDeserializer<>(NotificationEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        builder.stream(topicConfig.NOTIFICATION_RECEIVED_REQUEST, Consumed.with(
                        Serdes.serdeFrom(new StringSerializer(), new StringDeserializer()),
                        Serdes.serdeFrom(new JsonSerializer<>(), deserializer)))
                .to(topicConfig.NOTIFICATION_STORED_REQUEST, Produced.with(
                        Serdes.serdeFrom(new StringSerializer(), new StringDeserializer()),
                        Serdes.serdeFrom(new JsonSerializer<>(), deserializer)));

        Properties basicConfig = getBasicConfigProperty();
        basicConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "Notification-Admin-Application");
        basicConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        basicConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, new JsonSerde<>(NotificationEvent.class).getClass());
        basicConfig.put(JsonDeserializer.KEY_DEFAULT_TYPE, String.class);
        basicConfig.put(JsonDeserializer.VALUE_DEFAULT_TYPE, NotificationEvent.class);
        KafkaStreams streams = new KafkaStreams(builder.build(), basicConfig);
        streams.start();
    }

}

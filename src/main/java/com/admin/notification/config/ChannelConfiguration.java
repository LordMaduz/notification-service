package com.admin.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "events")
@Data
public class ChannelConfiguration {

    private Map<String, Map<String, Object>> channels;

}

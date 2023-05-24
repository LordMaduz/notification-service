package com.admin.notification.util;

import com.admin.notification.mjml.MJMLRenderService;
import com.admin.notification.model.enums.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationUtil {
    private final JSONUtil<Map> jsonUtil;

    private final MJMLRenderService mjmlRenderService;

    public Object toBody(String payload, String type, Channel channel, Map<String, Object> map) {
        if (type.equals("json")) {
            return jsonUtil.convertTo(payload, Map.class);
        } else if (type.equals("mjml")) {
            return mjmlRenderService.process(payload, map);
        }
        return payload;
    }

    public String formatChannelKey(String key) {
        return key.replaceAll("_", ".");
    }
}

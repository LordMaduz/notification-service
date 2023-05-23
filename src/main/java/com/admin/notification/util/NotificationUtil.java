package com.admin.notification.util;

import com.admin.notification.mjml.MJMLHTMLProcessor;
import com.admin.notification.model.enums.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationUtil {
    private final MJMLHTMLProcessor mjmlhtmlProcessor;
    private final JSONUtil<Map> jsonUtil;

    public Object toBody(String payload, String type, Map<String, Object> map) {
        if (type.equals("json")) {
            return jsonUtil.convertTo(payload, Map.class);
        } else if (type.equals("mjml")) {
            return mjmlhtmlProcessor.process(payload, map);
        }
        return payload;
    }

    public String formatChannelKey(String key) {
        return key.replaceAll("_", ".");
    }
}

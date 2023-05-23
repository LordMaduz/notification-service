package com.admin.notification.vo;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent<T> implements Serializable {
    private String eventId;
    private String userId;
    private T object;
}

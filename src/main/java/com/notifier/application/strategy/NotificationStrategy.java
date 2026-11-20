package com.notifier.application.strategy;

import com.notifier.application.dto.event.NotificationEvent;

public interface NotificationStrategy {
    boolean supports(NotificationEvent.NotificationType type);
    void send(NotificationEvent event);
}

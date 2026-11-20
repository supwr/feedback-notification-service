package com.notifier.infrastructure.exception;

public class SesSendException extends NotificationInfrastructureException {
    public SesSendException(String notificationId, Throwable cause) {
        super("Failed to send notification " + notificationId + " through SES", cause);
    }
}

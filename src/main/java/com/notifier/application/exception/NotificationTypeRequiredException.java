package com.notifier.application.exception;

public class NotificationTypeRequiredException extends NotificationValidationException {
    public NotificationTypeRequiredException() {
        super("Notification type is required");
    }
}

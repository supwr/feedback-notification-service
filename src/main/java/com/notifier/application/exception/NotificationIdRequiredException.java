package com.notifier.application.exception;

public class NotificationIdRequiredException extends NotificationValidationException {
    public NotificationIdRequiredException() {
        super("notificationId is required");
    }
}

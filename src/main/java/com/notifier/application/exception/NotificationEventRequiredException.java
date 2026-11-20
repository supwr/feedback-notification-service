package com.notifier.application.exception;

public class NotificationEventRequiredException extends NotificationValidationException {
    public NotificationEventRequiredException() {
        super("Notification event is required");
    }
}

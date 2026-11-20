package com.notifier.application.exception;

public class UnsupportedNotificationTypeException extends NotificationValidationException {
    public UnsupportedNotificationTypeException(String type) {
        super("Unsupported notification type: " + type);
    }
}

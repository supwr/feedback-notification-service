package com.notifier.application.exception;

public class NotificationBodyRequiredException extends NotificationValidationException {
    public NotificationBodyRequiredException() {
        super("body is required");
    }
}

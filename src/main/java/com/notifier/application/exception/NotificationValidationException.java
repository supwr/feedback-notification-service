package com.notifier.application.exception;

public class NotificationValidationException extends RuntimeException {
    public NotificationValidationException(String message) {
        super(message);
    }
}

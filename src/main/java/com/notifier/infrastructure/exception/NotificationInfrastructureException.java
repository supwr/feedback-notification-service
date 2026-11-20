package com.notifier.infrastructure.exception;

public class NotificationInfrastructureException extends RuntimeException {
    public NotificationInfrastructureException(String message) {
        super(message);
    }

    public NotificationInfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}

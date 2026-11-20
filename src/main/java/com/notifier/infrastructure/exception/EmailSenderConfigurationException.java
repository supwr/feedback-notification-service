package com.notifier.infrastructure.exception;

public class EmailSenderConfigurationException extends NotificationInfrastructureException {
    public EmailSenderConfigurationException() {
        super("notification.email.sender is not configured");
    }
}

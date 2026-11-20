package com.notifier.infrastructure.exception;

public class SqsMessageProcessingException extends NotificationInfrastructureException {
    public SqsMessageProcessingException(String messageId, Throwable cause) {
        super("Failed to process SQS message " + messageId, cause);
    }
}

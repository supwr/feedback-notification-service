package com.notifier.application.exception;

public class InvalidAttachmentException extends NotificationValidationException {
    public InvalidAttachmentException() {
        super("Invalid attachment: bucket and key are required");
    }
}

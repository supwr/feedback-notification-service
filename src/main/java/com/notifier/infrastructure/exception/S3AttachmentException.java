package com.notifier.infrastructure.exception;

public class S3AttachmentException extends NotificationInfrastructureException {
    public S3AttachmentException(String bucket, String key, Throwable cause) {
        super("Failed to read S3 attachment bucket=" + bucket + " key=" + key, cause);
    }
}

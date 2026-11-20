package com.notifier.application.exception;

public class RecipientRequiredException extends NotificationValidationException {
    public RecipientRequiredException() {
        super("At least one To recipient is required");
    }
}

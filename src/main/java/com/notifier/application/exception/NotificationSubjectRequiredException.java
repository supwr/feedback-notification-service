package com.notifier.application.exception;

public class NotificationSubjectRequiredException extends NotificationValidationException {
    public NotificationSubjectRequiredException() {
        super("subject is required");
    }
}

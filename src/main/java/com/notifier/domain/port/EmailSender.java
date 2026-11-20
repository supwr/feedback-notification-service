package com.notifier.domain.port;

import com.notifier.domain.model.EmailNotification;

public interface EmailSender {
    void send(EmailNotification notification);
}

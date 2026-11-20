package com.notifier.application.strategy;

import com.notifier.application.dto.event.NotificationEvent;
import com.notifier.domain.model.AttachmentContent;
import com.notifier.domain.model.EmailNotification;
import com.notifier.domain.port.AttachmentStorage;
import com.notifier.domain.port.EmailSender;
import com.notifier.application.exception.RecipientRequiredException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailNotificationStrategyTest {

    @Test
    void shouldLoadS3AttachmentAndSendEmail() {
        RecordingEmailSender sender = new RecordingEmailSender();
        RecordingAttachmentStorage storage = new RecordingAttachmentStorage();
        EmailNotificationStrategy strategy = new EmailNotificationStrategy(sender, storage);

        NotificationEvent event = NotificationEvent.builder()
                .notificationId("notification-123")
                .type(NotificationEvent.NotificationType.EMAIL)
                .source("feedback")
                .recipient(NotificationEvent.Recipient.builder()
                        .to(List.of("suporte@example.com"))
                        .cc(List.of())
                        .bcc(List.of())
                        .build())
                .subject("Nova avaliação crítica")
                .body(NotificationEvent.Body.builder()
                        .text("Texto")
                        .html("<p>Texto</p>")
                        .build())
                .replyTo(List.of())
                .attachments(List.of(NotificationEvent.Attachment.builder()
                        .bucket("notifications")
                        .key("feedback/report.pdf")
                        .fileName("report.pdf")
                        .contentType("application/pdf")
                        .build()))
                .metadata(Map.of("feedbackId", "feedback-123"))
                .build();

        strategy.send(event);

        assertEquals("notifications", storage.bucket);
        assertEquals("feedback/report.pdf", storage.key);
        assertEquals("report.pdf", sender.notification.getAttachments().get(0).getFileName());
        assertEquals("notification-123", sender.notification.getNotificationId());
    }

    @Test
    void shouldThrowTypedExceptionWhenRecipientIsMissing() {
        EmailNotificationStrategy strategy = new EmailNotificationStrategy(
                new RecordingEmailSender(), new RecordingAttachmentStorage());
        NotificationEvent event = NotificationEvent.builder()
                .notificationId("notification-123")
                .recipient(NotificationEvent.Recipient.builder().to(List.of()).build())
                .subject("Subject")
                .body(NotificationEvent.Body.builder().text("Text").build())
                .build();

        assertThrows(RecipientRequiredException.class, () -> strategy.send(event));
    }

    private static class RecordingEmailSender implements EmailSender {
        private EmailNotification notification;

        @Override
        public void send(EmailNotification notification) {
            this.notification = notification;
        }
    }

    private static class RecordingAttachmentStorage implements AttachmentStorage {
        private String bucket;
        private String key;

        @Override
        public AttachmentContent get(String bucket, String key, String fileName, String contentType) {
            this.bucket = bucket;
            this.key = key;
            return new AttachmentContent(fileName, contentType,
                    "pdf".getBytes(StandardCharsets.UTF_8));
        }
    }
}

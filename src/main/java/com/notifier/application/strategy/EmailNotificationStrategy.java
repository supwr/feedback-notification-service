package com.notifier.application.strategy;

import com.notifier.application.dto.event.NotificationEvent;
import com.notifier.application.exception.InvalidAttachmentException;
import com.notifier.application.exception.NotificationEventRequiredException;
import com.notifier.application.exception.NotificationIdRequiredException;
import com.notifier.application.exception.NotificationSubjectRequiredException;
import com.notifier.application.exception.NotificationBodyRequiredException;
import com.notifier.application.exception.RecipientRequiredException;
import com.notifier.domain.model.AttachmentContent;
import com.notifier.domain.model.EmailBody;
import com.notifier.domain.model.EmailNotification;
import com.notifier.domain.model.EmailRecipient;
import com.notifier.domain.port.AttachmentStorage;
import com.notifier.domain.port.EmailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class EmailNotificationStrategy implements NotificationStrategy {
    private final EmailSender emailSender;
    private final AttachmentStorage attachmentStorage;

    @Override
    public boolean supports(NotificationEvent.NotificationType type) {
        return type == NotificationEvent.NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationEvent event) {
        validate(event);

        List<AttachmentContent> attachments = new ArrayList<>();
        if (event.getAttachments() != null) {
            for (NotificationEvent.Attachment attachment : event.getAttachments()) {
                if (attachment == null
                        || attachment.effectiveBucket() == null
                        || attachment.effectiveKey() == null) {
                    throw new InvalidAttachmentException();
                }
                attachments.add(attachmentStorage.get(
                        attachment.effectiveBucket(),
                        attachment.effectiveKey(),
                        attachment.effectiveFileName(),
                        attachment.getContentType()
                ));
            }
        }

        NotificationEvent.Recipient recipient = event.getRecipient();
        NotificationEvent.Body body = event.getBody();

        EmailNotification notification = new EmailNotification(
                event.getNotificationId(),
                event.getSource(),
                new EmailRecipient(
                        recipient == null ? null : recipient.getTo(),
                        recipient == null ? null : recipient.getCc(),
                        recipient == null ? null : recipient.getBcc()),
                event.getSubject(),
                new EmailBody(body == null ? null : body.getText(), body == null ? null : body.getHtml()),
                event.getReplyTo(),
                attachments,
                event.getMetadata()
        );

        emailSender.send(notification);
    }

    private void validate(NotificationEvent event) {
        if (event == null) {
            throw new NotificationEventRequiredException();
        }
        if (event.getNotificationId() == null || event.getNotificationId().isBlank()) {
            throw new NotificationIdRequiredException();
        }
        if (event.getRecipient() == null
                || event.getRecipient().getTo() == null
                || event.getRecipient().getTo().isEmpty()) {
            throw new RecipientRequiredException();
        }
        if (event.getSubject() == null || event.getSubject().isBlank()) {
            throw new NotificationSubjectRequiredException();
        }
        if (event.getBody() == null) {
            throw new NotificationBodyRequiredException();
        }
    }
}

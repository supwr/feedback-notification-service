package com.notifier.domain.model;

import java.util.List;
import java.util.Map;

public class EmailNotification {
    private final String notificationId;
    private final String source;
    private final EmailRecipient recipient;
    private final String subject;
    private final EmailBody body;
    private final List<String> replyTo;
    private final List<AttachmentContent> attachments;
    private final Map<String, Object> metadata;

    public EmailNotification(String notificationId, String source, EmailRecipient recipient,
                             String subject, EmailBody body, List<String> replyTo,
                             List<AttachmentContent> attachments, Map<String, Object> metadata) {
        this.notificationId = notificationId;
        this.source = source;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.replyTo = List.copyOf(replyTo == null ? List.of() : replyTo);
        this.attachments = List.copyOf(attachments == null ? List.of() : attachments);
        this.metadata = Map.copyOf(metadata == null ? Map.of() : metadata);
    }

    public String getNotificationId() { return notificationId; }
    public String getSource() { return source; }
    public EmailRecipient getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public EmailBody getBody() { return body; }
    public List<String> getReplyTo() { return replyTo; }
    public List<AttachmentContent> getAttachments() { return attachments; }
    public Map<String, Object> getMetadata() { return metadata; }
}

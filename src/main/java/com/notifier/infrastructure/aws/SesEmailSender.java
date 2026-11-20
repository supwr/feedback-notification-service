package com.notifier.infrastructure.aws;

import com.notifier.domain.model.AttachmentContent;
import com.notifier.domain.model.EmailNotification;
import com.notifier.domain.port.EmailSender;
import com.notifier.infrastructure.exception.EmailSenderConfigurationException;
import com.notifier.infrastructure.exception.SesSendException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SesEmailSender implements EmailSender {
    private final SesClient sesClient;

    @ConfigProperty(name = "notification.email.sender")
    String sender;

    @Override
    public void send(EmailNotification notification) {
        if (sender == null || sender.isBlank()) {
            throw new EmailSenderConfigurationException();
        }

        Destination destination = Destination.builder()
                .toAddresses(notification.getRecipient().getTo())
                .ccAddresses(notification.getRecipient().getCc())
                .bccAddresses(notification.getRecipient().getBcc())
                .build();

        try {
        if (notification.getAttachments().isEmpty()) {
            Content subject = Content.builder().charset("UTF-8").data(notification.getSubject()).build();
            Body body = Body.builder()
                    .text(textContent(notification.getBody().getText()))
                    .html(htmlContent(notification.getBody().getHtml()))
                    .build();

            sesClient.sendEmail(SendEmailRequest.builder()
                    .source(sender)
                    .destination(destination)
                    .replyToAddresses(notification.getReplyTo())
                    .message(Message.builder().subject(subject).body(body).build())
                    .build());
            return;
        }

        String raw = buildRawMime(notification, sender);
        sesClient.sendRawEmail(SendRawEmailRequest.builder()
                .source(sender)
                .destinations(allRecipients(notification))
                .rawMessage(b -> b.data(SdkBytes.fromString(raw, StandardCharsets.UTF_8)))
                .build());
        } catch (Exception exception) {
            throw new SesSendException(notification.getNotificationId(), exception);
        }
    }

    private Content textContent(String text) {
        return text == null
                ? Content.builder().charset("UTF-8").data("").build()
                : Content.builder().charset("UTF-8").data(text).build();
    }

    private Content htmlContent(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }
        return Content.builder().charset("UTF-8").data(html).build();
    }

    private List<String> allRecipients(EmailNotification notification) {
        java.util.ArrayList<String> recipients = new java.util.ArrayList<>();
        recipients.addAll(notification.getRecipient().getTo());
        recipients.addAll(notification.getRecipient().getCc());
        recipients.addAll(notification.getRecipient().getBcc());
        return recipients;
    }

    private String buildRawMime(EmailNotification notification, String sender) {
        String boundaryAlternative = "alternative-" + UUID.randomUUID();
        String boundaryMixed = "mixed-" + UUID.randomUUID();
        StringBuilder mime = new StringBuilder();

        mime.append("From: ").append(sender).append("\r\n");
        mime.append("To: ").append(String.join(", ", notification.getRecipient().getTo())).append("\r\n");
        if (!notification.getRecipient().getCc().isEmpty()) {
            mime.append("Cc: ").append(String.join(", ", notification.getRecipient().getCc())).append("\r\n");
        }
        if (!notification.getReplyTo().isEmpty()) {
            mime.append("Reply-To: ").append(String.join(", ", notification.getReplyTo())).append("\r\n");
        }
        mime.append("Subject: ").append(rfc2047(notification.getSubject())).append("\r\n");
        mime.append("MIME-Version: 1.0\r\n");
        mime.append("Content-Type: multipart/mixed; boundary=\"").append(boundaryMixed).append("\"\r\n\r\n");
        mime.append("--").append(boundaryMixed).append("\r\n");
        mime.append("Content-Type: multipart/alternative; boundary=\"").append(boundaryAlternative).append("\"\r\n\r\n");

        if (notification.getBody().getText() != null) {
            mime.append("--").append(boundaryAlternative).append("\r\n");
            mime.append("Content-Type: text/plain; charset=UTF-8\r\n");
            mime.append("Content-Transfer-Encoding: base64\r\n\r\n");
            mime.append(base64Lines(notification.getBody().getText())).append("\r\n");
        }
        if (notification.getBody().getHtml() != null) {
            mime.append("--").append(boundaryAlternative).append("\r\n");
            mime.append("Content-Type: text/html; charset=UTF-8\r\n");
            mime.append("Content-Transfer-Encoding: base64\r\n\r\n");
            mime.append(base64Lines(notification.getBody().getHtml())).append("\r\n");
        }
        mime.append("--").append(boundaryAlternative).append("--\r\n");

        for (AttachmentContent attachment : notification.getAttachments()) {
            mime.append("--").append(boundaryMixed).append("\r\n");
            mime.append("Content-Type: ").append(attachment.getContentType()).append("; name=\"").append(attachment.getFileName()).append("\"\r\n");
            mime.append("Content-Description: ").append(attachment.getFileName()).append("\r\n");
            mime.append("Content-Disposition: attachment; filename=\"").append(attachment.getFileName()).append("\"\r\n");
            mime.append("Content-Transfer-Encoding: base64\r\n\r\n");
            mime.append(base64Lines(attachment.getContent())).append("\r\n");
        }
        mime.append("--").append(boundaryMixed).append("--\r\n");
        return mime.toString();
    }

    private String rfc2047(String value) {
        if (value == null) {
            return "";
        }
        return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)) + "?=";
    }

    private String base64Lines(String value) {
        return base64Lines(value.getBytes(StandardCharsets.UTF_8));
    }

    private String base64Lines(byte[] value) {
        String encoded = Base64.getEncoder().encodeToString(value);
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < encoded.length(); i += 76) {
            if (i > 0) {
                lines.append("\r\n");
            }
            lines.append(encoded, i, Math.min(i + 76, encoded.length()));
        }
        return lines.toString();
    }
}

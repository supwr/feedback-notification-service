package com.notifier.application.dto.event;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class NotificationEvent {
    private String notificationId;
    private NotificationType type;
    private String source;
    private Recipient recipient;
    private String subject;
    private Body body;
    private List<Attachment> attachments;
    private List<String> replyTo;
    private Map<String, Object> metadata;

    public enum NotificationType {
        EMAIL,
        @JsonEnumDefaultValue
        UNSUPPORTED
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @RegisterForReflection
    public static class Recipient {
        private List<String> to;
        private List<String> cc;
        private List<String> bcc;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @RegisterForReflection
    public static class Body {
        private String text;
        private String html;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @RegisterForReflection
    public static class Attachment {
        private String bucket;
        private String bucketName;
        private String key;
        private String objectKey;
        private String fileName;
        private String name;
        private String contentType;

        public String effectiveBucket() {
            if (bucket != null && !bucket.isBlank()) return bucket;
            return bucketName;
        }

        public String effectiveKey() {
            if (key != null && !key.isBlank()) return key;
            return objectKey;
        }

        public String effectiveFileName() {
            if (fileName != null && !fileName.isBlank()) return fileName;
            if (name != null && !name.isBlank()) return name;

            String resolvedKey = effectiveKey();
            if (resolvedKey == null) return "attachment";
            int separator = Math.max(resolvedKey.lastIndexOf('/'), resolvedKey.lastIndexOf('\\'));
            return separator >= 0 ? resolvedKey.substring(separator + 1) : resolvedKey;
        }
    }
}

package com.notifier.domain.port;

import com.notifier.domain.model.AttachmentContent;

public interface AttachmentStorage {
    AttachmentContent get(String bucket, String key, String fileName, String contentType);
}

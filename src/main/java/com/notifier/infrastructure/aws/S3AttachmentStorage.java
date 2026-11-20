package com.notifier.infrastructure.aws;

import com.notifier.domain.model.AttachmentContent;
import com.notifier.domain.port.AttachmentStorage;
import com.notifier.infrastructure.exception.S3AttachmentException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class S3AttachmentStorage implements AttachmentStorage {
    private final S3Client s3Client;

    @Override
    public AttachmentContent get(String bucket, String key, String fileName, String contentType) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> response;
        try {
            response = s3Client.getObjectAsBytes(request);
        } catch (Exception exception) {
            throw new S3AttachmentException(bucket, key, exception);
        }
        String resolvedContentType = contentType;
        if (resolvedContentType == null || resolvedContentType.isBlank()) {
            resolvedContentType = response.response().contentType() == null
                    ? "application/octet-stream"
                    : response.response().contentType();
        }

        return new AttachmentContent(fileName, resolvedContentType, response.asByteArray());
    }
}

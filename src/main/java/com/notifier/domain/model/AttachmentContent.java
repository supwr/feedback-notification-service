package com.notifier.domain.model;

public class AttachmentContent {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public AttachmentContent(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public byte[] getContent() { return content; }
}

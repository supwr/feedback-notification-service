package com.notifier.domain.model;

public class EmailBody {
    private final String text;
    private final String html;

    public EmailBody(String text, String html) {
        this.text = text;
        this.html = html;
    }

    public String getText() { return text; }
    public String getHtml() { return html; }
}

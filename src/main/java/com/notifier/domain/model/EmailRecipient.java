package com.notifier.domain.model;

import java.util.List;

public class EmailRecipient {
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;

    public EmailRecipient(List<String> to, List<String> cc, List<String> bcc) {
        this.to = List.copyOf(to == null ? List.of() : to);
        this.cc = List.copyOf(cc == null ? List.of() : cc);
        this.bcc = List.copyOf(bcc == null ? List.of() : bcc);
    }

    public List<String> getTo() { return to; }
    public List<String> getCc() { return cc; }
    public List<String> getBcc() { return bcc; }
}

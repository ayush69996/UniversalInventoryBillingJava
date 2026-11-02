package com.inventory.model;

import java.time.LocalDateTime;

public abstract class Report {

    private final String title;
    private final LocalDateTime generatedAt;

    public Report(String title) {
        this.title = title;
        this.generatedAt = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public abstract String generate();
}
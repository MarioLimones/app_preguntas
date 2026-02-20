package com.app.quiz.features.upload;

import java.util.List;

public class QuestionUploadResult {
    private final int createdCount;
    private final List<String> errors;

    public QuestionUploadResult(int createdCount, List<String> errors) {
        this.createdCount = createdCount;
        this.errors = errors;
    }

    public int getCreatedCount() {
        return createdCount;
    }

    public List<String> getErrors() {
        return errors;
    }
}

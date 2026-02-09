package com.app.preguntas.preguntas.sc.service;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleChoiceQuestionImportResult {

    private final int total;
    private final List<SingleChoiceQuestion> created;
    private final List<String> errors;

    public SingleChoiceQuestionImportResult(int total,
                                            List<SingleChoiceQuestion> created,
                                            List<String> errors) {
        this.total = total;
        this.created = created != null ? new ArrayList<>(created) : new ArrayList<>();
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public int getTotal() {
        return total;
    }

    public int getCreatedCount() {
        return created.size();
    }

    public List<SingleChoiceQuestion> getCreated() {
        return Collections.unmodifiableList(created);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}

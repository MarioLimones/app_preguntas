package com.app.preguntas.preguntas.vf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequenceSession implements Serializable {

    private final List<Long> questionIds = new ArrayList<>();
    private final Map<Long, Boolean> answers = new HashMap<>();
    private int currentIndex = 0;

    public SequenceSession() {
    }

    public SequenceSession(List<Long> ids) {
        if (ids != null) {
            this.questionIds.addAll(ids);
        }
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public Map<Long, Boolean> getAnswers() {
        return answers;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public boolean hasNext() {
        return currentIndex < questionIds.size() - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public Long getCurrentQuestionId() {
        if (questionIds.isEmpty()) {
            return null;
        }
        return questionIds.get(Math.min(currentIndex, questionIds.size() - 1));
    }

    public int getTotal() {
        return questionIds.size();
    }

    public int getTotalQuestions() {
        return questionIds.size();
    }

    public int getAnsweredCount() {
        return answers.size();
    }
}

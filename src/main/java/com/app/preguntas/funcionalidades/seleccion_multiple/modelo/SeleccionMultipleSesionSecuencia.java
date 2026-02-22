package com.app.preguntas.funcionalidades.seleccion_multiple.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeleccionMultipleSesionSecuencia implements Serializable {

    private final List<Long> questionIds = new ArrayList<>();
    private final Map<Long, List<Integer>> answers = new HashMap<>();
    private int currentIndex = 0;

    public SeleccionMultipleSesionSecuencia() {
    }

    public SeleccionMultipleSesionSecuencia(List<Long> ids) {
        if (ids != null) {
            this.questionIds.addAll(ids);
        }
    }

    public List<Long> getQuestionIds() {
        return questionIds;
    }

    public Map<Long, List<Integer>> getAnswers() {
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






package com.app.preguntas.funcionalidades.seleccion_multiple.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SeleccionMultipleItemRevision {

    private final PreguntaSeleccionMultiple question;
    private final List<Integer> userAnswers;

    public SeleccionMultipleItemRevision(PreguntaSeleccionMultiple question, List<Integer> userAnswers) {
        this.question = question;
        this.userAnswers = userAnswers != null ? new ArrayList<>(userAnswers) : new ArrayList<>();
    }

    public PreguntaSeleccionMultiple getQuestion() {
        return question;
    }

    public List<Integer> getUserAnswers() {
        return userAnswers;
    }

    public boolean isCorrect() {
        if (question == null) {
            return false;
        }
        Set<Integer> correct = toSet(question.getCorrectIndexes());
        Set<Integer> user = toSet(userAnswers);
        if (correct.isEmpty()) {
            return false;
        }
        return correct.equals(user);
    }

    public String getUserAnswerDisplay() {
        return buildDisplay(userAnswers);
    }

    public String getCorrectAnswerDisplay() {
        if (question == null) {
            return "-";
        }
        return buildDisplay(question.getCorrectIndexes());
    }

    private String buildDisplay(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return "Sin respuesta";
        }
        List<Integer> sorted = new ArrayList<>(new HashSet<>(values));
        sorted.sort(Integer::compareTo);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sorted.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(sorted.get(i) + 1);
        }
        return builder.toString();
    }

    private Set<Integer> toSet(List<Integer> values) {
        Set<Integer> result = new HashSet<>();
        if (values != null) {
            result.addAll(values);
        }
        return result;
    }
}





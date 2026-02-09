package com.app.preguntas.preguntas.sc.service;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;

import java.util.HashSet;
import java.util.Set;

public final class SingleChoiceQuestionValidator {

    private SingleChoiceQuestionValidator() {
    }

    public static String validate(SingleChoiceQuestion question) {
        if (question == null) {
            return "La pregunta es obligatoria.";
        }
        if (question.getStatement() == null || question.getStatement().trim().isEmpty()) {
            return "La pregunta es obligatoria.";
        }
        if (question.getOptions() == null || question.getOptions().size() < 2) {
            return "Debes ingresar al menos dos opciones.";
        }
        Set<String> seen = new HashSet<>();
        for (String option : question.getOptions()) {
            if (option == null || option.trim().isEmpty()) {
                return "No se permiten opciones vacias.";
            }
            String normalized = option.trim().toLowerCase();
            if (!seen.add(normalized)) {
                return "No se permiten opciones duplicadas.";
            }
        }
        if (question.getCorrectIndex() == null) {
            return "Debes indicar la opcion correcta.";
        }
        if (question.getCorrectIndex() < 0 || question.getCorrectIndex() >= question.getOptions().size()) {
            return "La opcion correcta es invalida.";
        }
        return null;
    }
}

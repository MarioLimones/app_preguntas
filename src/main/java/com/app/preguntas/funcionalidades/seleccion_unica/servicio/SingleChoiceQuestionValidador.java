package com.app.preguntas.funcionalidades.seleccion_unica.service;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;

import java.util.HashSet;
import java.util.Set;

public final class SeleccionUnicaQuestionValidador {

    private SeleccionUnicaQuestionValidador() {
    }

    public static String validate(PreguntaSeleccionUnica question) {
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





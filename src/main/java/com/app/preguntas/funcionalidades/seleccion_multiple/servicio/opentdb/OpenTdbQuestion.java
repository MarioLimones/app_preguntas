package com.app.preguntas.funcionalidades.seleccion_multiple.servicio.opentdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenTdbQuestion(
    @JsonProperty("Categoria") String Categoria,
    @JsonProperty("type") String type,
    @JsonProperty("difficulty") String difficulty,
    @JsonProperty("question") String question,
    @JsonProperty("correct_answer") String correctAnswer,
    @JsonProperty("incorrect_answers") List<String> incorrectAnswers
) {
}





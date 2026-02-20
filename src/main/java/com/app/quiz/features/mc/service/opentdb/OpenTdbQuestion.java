package com.app.quiz.features.mc.service.opentdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenTdbQuestion(
    @JsonProperty("category") String category,
    @JsonProperty("type") String type,
    @JsonProperty("difficulty") String difficulty,
    @JsonProperty("question") String question,
    @JsonProperty("correct_answer") String correctAnswer,
    @JsonProperty("incorrect_answers") List<String> incorrectAnswers
) {
}




package com.app.preguntas.preguntas.vf.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrueFalseQuestion {

    private Long id;

    @NotBlank(message = "La pregunta es obligatoria.")
    private String statement;

    @NotNull(message = "Debes indicar la respuesta correcta.")
    private Boolean correctAnswer;

    private String explanation;

    public TrueFalseQuestion() {
    }

    public TrueFalseQuestion(Long id, String statement, Boolean correctAnswer, String explanation) {
        this.id = id;
        this.statement = statement;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

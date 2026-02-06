package com.app.preguntas.preguntas.vf.model;

import jakarta.validation.constraints.NotNull;

public class AnswerForm {

    @NotNull(message = "Selecciona Verdadero o Falso.")
    private Boolean userAnswer;

    public Boolean getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Boolean userAnswer) {
        this.userAnswer = userAnswer;
    }
}

package com.app.preguntas.funcionalidades.verdadero_falso.modelo;

import jakarta.validation.constraints.NotNull;

public class FormularioRespuesta {

    @NotNull(message = "Selecciona Verdadero o Falso.")
    private Boolean userAnswer;

    public Boolean getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Boolean userAnswer) {
        this.userAnswer = userAnswer;
    }
}






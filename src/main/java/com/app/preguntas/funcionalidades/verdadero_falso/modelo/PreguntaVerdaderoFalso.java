package com.app.preguntas.funcionalidades.verdadero_falso.model;

import com.app.preguntas.nucleo.PreguntaBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "vf_questions")
public class PreguntaVerdaderoFalso extends PreguntaBase {

    @NotNull(message = "Debes indicar la respuesta correcta.")
    private Boolean correctAnswer;

    public PreguntaVerdaderoFalso() {
        super();
    }

    public PreguntaVerdaderoFalso(Long id, String statement, Boolean correctAnswer, String explanation) {
        super(id, statement, explanation);
        this.correctAnswer = correctAnswer;
    }

    @Override
    @NotBlank(message = "La pregunta es obligatoria.")
    public String getStatement() {
        return super.getStatement();
    }

    @Override
    public void setStatement(String statement) {
        super.setStatement(statement);
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}





package com.app.quiz.features.sc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SingleChoiceQuestionForm {

    @NotBlank(message = "La pregunta es obligatoria.")
    private String statement;

    @NotBlank(message = "Debes ingresar al menos dos opciones.")
    private String optionsText;

    @NotNull(message = "Debes indicar el numero de la opcion correcta.")
    private Integer correctIndex;

    private String explanation;

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOptionsText() {
        return optionsText;
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}




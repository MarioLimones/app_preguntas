package com.app.preguntas.preguntas.mc.model;

import jakarta.validation.constraints.NotBlank;

public class MultipleChoiceQuestionForm {

    @NotBlank(message = "La pregunta es obligatoria.")
    private String statement;

    @NotBlank(message = "Debes ingresar al menos dos opciones.")
    private String optionsText;

    @NotBlank(message = "Debes indicar al menos una opcion correcta.")
    private String correctIndexesText;

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

    public String getCorrectIndexesText() {
        return correctIndexesText;
    }

    public void setCorrectIndexesText(String correctIndexesText) {
        this.correctIndexesText = correctIndexesText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

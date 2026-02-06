package com.app.preguntas.preguntas.sc.model;

import jakarta.validation.constraints.NotNull;

public class SingleChoiceAnswerForm {

    @NotNull(message = "Selecciona una opcion.")
    private Integer selectedIndex;

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}

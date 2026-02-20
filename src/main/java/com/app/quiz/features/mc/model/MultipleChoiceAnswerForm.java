package com.app.quiz.features.mc.model;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceAnswerForm {

    @NotEmpty(message = "Selecciona al menos una opcion.")
    private List<Integer> selectedIndexes = new ArrayList<>();

    public List<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public void setSelectedIndexes(List<Integer> selectedIndexes) {
        this.selectedIndexes = selectedIndexes != null ? selectedIndexes : new ArrayList<>();
    }
}




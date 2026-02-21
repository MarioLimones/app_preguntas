package com.app.preguntas.funcionalidades.seleccion_unica.model;

import jakarta.validation.constraints.NotNull;

public class SeleccionUnicaFormularioRespuesta {

    @NotNull(message = "Selecciona una opcion.")
    private Integer selectedIndex;

    public Integer getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(Integer selectedIndex) {
        this.selectedIndex = selectedIndex;
    }
}





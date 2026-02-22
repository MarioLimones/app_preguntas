package com.app.preguntas.funcionalidades.seleccion_unica.servicio;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeleccionUnicaQuestionImportResult {

    private final int total;
    private final List<PreguntaSeleccionUnica> created;
    private final List<String> errors;

    public SeleccionUnicaQuestionImportResult(int total,
                                            List<PreguntaSeleccionUnica> created,
                                            List<String> errors) {
        this.total = total;
        this.created = created != null ? new ArrayList<>(created) : new ArrayList<>();
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public int getTotal() {
        return total;
    }

    public int getCreatedCount() {
        return created.size();
    }

    public List<PreguntaSeleccionUnica> getCreated() {
        return Collections.unmodifiableList(created);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}







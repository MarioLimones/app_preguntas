package com.app.preguntas.funcionalidades.seleccion_multiple.servicio.opentdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenTdbResponse(
    @JsonProperty("response_code") int responseCode,
    @JsonProperty("results") List<OpenTdbQuestion> results
) {
}





package com.app.preguntas.preguntas.common.service.libretranslate;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LibreTranslateResponse(
    @JsonProperty("translatedText") Object translatedText
) {
}

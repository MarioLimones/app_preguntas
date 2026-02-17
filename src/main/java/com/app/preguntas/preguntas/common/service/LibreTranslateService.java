package com.app.preguntas.preguntas.common.service;

import com.app.preguntas.preguntas.common.service.libretranslate.LibreTranslateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.Duration;

@Service
public class LibreTranslateService {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    private final boolean enabled;

    public LibreTranslateService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${libretranslate.api.base-url:http://127.0.0.1:5000/translate}") String baseUrl,
            @Value("${libretranslate.api.key:}") String apiKey,
            @Value("${libretranslate.enabled:true}") boolean enabled) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.enabled = enabled;
    }

    public List<String> translateToSpanish(List<String> texts) {
        if (!enabled || texts == null || texts.isEmpty()) {
            return texts == null ? List.of() : texts;
        }
        if ((apiKey == null || apiKey.isBlank()) && baseUrl.contains("libretranslate.com")) {
            return texts;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("q", texts);
        payload.put("source", "en");
        payload.put("target", "es");
        payload.put("format", "text");
        if (apiKey != null && !apiKey.isBlank()) {
            payload.put("api_key", apiKey);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        LibreTranslateResponse response;
        try {
            response = restTemplate.postForObject(
                    baseUrl,
                    new HttpEntity<>(payload, headers),
                    LibreTranslateResponse.class);
        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest ex) {
            return texts;
        } catch (RestClientException ex) {
            System.err.println("Error en LibreTranslate: " + ex);
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo traducir con LibreTranslate. Revisa la configuracion de la API.",
                    ex);
        }

        if (response == null || response.translatedText() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "LibreTranslate no devolvio traducciones.");
        }

        Object translated = response.translatedText();
        if (translated instanceof String text) {
            return List.of(text);
        }
        if (translated instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object item : list) {
                result.add(item == null ? "" : item.toString());
            }
            return result;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "LibreTranslate devolvio un formato inesperado.");
    }
}

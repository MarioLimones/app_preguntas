package com.app.preguntas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServicioTraduccionLibre {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final boolean enabled;

    public ServicioTraduccionLibre(RestTemplateBuilder restTemplateBuilder,
            @Value("${libretranslate.api.base-url}") String baseUrl,
            @Value("${libretranslate.enabled:true}") boolean enabled) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
        this.enabled = enabled;
    }

    public List<String> translateToSpanish(List<String> texts) {
        if (!enabled || texts == null || texts.isEmpty()) {
            return texts;
        }

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("q", texts);
            request.put("source", "en");
            request.put("target", "es");
            request.put("format", "text");

            Map<String, Object> response = restTemplate.postForObject(baseUrl, request, Map.class);
            if (response != null && response.containsKey("translatedText")) {
                Object translated = response.get("translatedText");
                if (translated instanceof List) {
                    return ((List<?>) translated).stream().map(Object::toString).collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling LibreTranslate: " + e.getMessage());
        }
        return texts;
    }
}



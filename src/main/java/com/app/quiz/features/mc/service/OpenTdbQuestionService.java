package com.app.quiz.features.mc.service;

import com.app.quiz.features.mc.model.MultipleChoiceQuestion;

import com.app.quiz.LibreTranslateService;
import com.app.quiz.features.mc.service.opentdb.OpenTdbQuestion;
import com.app.quiz.features.mc.service.opentdb.OpenTdbResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OpenTdbQuestionService {

    private static final int MAX_AMOUNT = 50;
    private static final int SUCCESS_CODE = 0;

    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final LibreTranslateService libreTranslateService;

    public OpenTdbQuestionService(
            @Value("${opentdb.api.base-url:https://opentdb.com/api.php}") String baseUrl,
            RestTemplateBuilder restTemplateBuilder,
            LibreTranslateService libreTranslateService) {
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplateBuilder.build();
        this.libreTranslateService = libreTranslateService;
    }

    public List<MultipleChoiceQuestion> fetchMultipleChoiceQuestions(int amount,
            Integer category,
            String difficulty) {
        int boundedAmount = Math.max(1, Math.min(amount, MAX_AMOUNT));
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("amount", boundedAmount)
                .queryParam("type", "multiple");

        if (category != null) {
            builder.queryParam("category", category);
        }
        if (difficulty != null && !difficulty.isBlank()) {
            builder.queryParam("difficulty", difficulty.trim().toLowerCase());
        }

        OpenTdbResponse response;
        try {
            response = restTemplate.getForObject(builder.toUriString(), OpenTdbResponse.class);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar OpenTDB.", ex);
        }

        if (response == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenTDB no devolvio datos.");
        }
        if (response.responseCode() != SUCCESS_CODE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenTDB devolvio codigo: " + response.responseCode());
        }

        List<MultipleChoiceQuestion> mapped = new ArrayList<>();
        if (response.results() != null) {
            for (OpenTdbQuestion question : response.results()) {
                mapped.add(mapQuestion(question));
            }
        }
        return mapped;
    }

    private MultipleChoiceQuestion mapQuestion(OpenTdbQuestion question) {
        String statement = HtmlUtils.htmlUnescape(question.question());
        List<String> incorrectOptions = new ArrayList<>();
        if (question.incorrectAnswers() != null) {
            for (String incorrect : question.incorrectAnswers()) {
                incorrectOptions.add(HtmlUtils.htmlUnescape(incorrect));
            }
        }

        String correct = HtmlUtils.htmlUnescape(question.correctAnswer());

        // Traducción de pregunta y opciones
        List<String> textsToTranslate = new ArrayList<>();
        textsToTranslate.add(statement);
        textsToTranslate.add(correct);
        textsToTranslate.addAll(incorrectOptions);

        try {
            System.out.println("Traduciendo pregunta: " + statement);
            List<String> translated = libreTranslateService.translateToSpanish(textsToTranslate);
            if (translated != null && translated.size() == textsToTranslate.size()) {
                statement = translated.get(0);
                correct = translated.get(1);
                incorrectOptions.clear();
                incorrectOptions.addAll(translated.subList(2, translated.size()));
                System.out.println("Traduccion completada con exito.");
            } else {
                System.out.println("Advertencia: El traductor devolvio un numero inconsistente de resultados.");
            }
        } catch (Exception e) {
            System.err.println("Error critico traduciendo pregunta: " + e.getMessage());
        }

        // Mezclar opciones
        List<String> options = new ArrayList<>();
        options.addAll(incorrectOptions);
        options.add(correct);
        Collections.shuffle(options);
        int correctIndex = options.indexOf(correct);
        if (correctIndex < 0) {
            correctIndex = 0;
        }

        String explanation = "Fuente: Open Trivia DB"
                + (question.category() != null ? " | Categoria: " + HtmlUtils.htmlUnescape(question.category()) : "")
                + (question.difficulty() != null ? " | Dificultad: " + HtmlUtils.htmlUnescape(question.difficulty())
                        : "");

        return new MultipleChoiceQuestion(
                null,
                statement,
                options,
                List.of(correctIndex),
                explanation);
    }
}




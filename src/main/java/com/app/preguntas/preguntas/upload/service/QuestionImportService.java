package com.app.preguntas.preguntas.upload.service;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
import com.app.preguntas.preguntas.mc.service.MultipleChoiceQuestionService;
import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionService;
import com.app.preguntas.preguntas.vf.model.TrueFalseQuestion;
import com.app.preguntas.preguntas.vf.service.TrueFalseQuestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class QuestionImportService {

    private final TrueFalseQuestionService trueFalseService;
    private final SingleChoiceQuestionService singleChoiceService;
    private final MultipleChoiceQuestionService multipleChoiceService;
    private final ObjectMapper objectMapper;

    public QuestionImportService(
            TrueFalseQuestionService trueFalseService,
            SingleChoiceQuestionService singleChoiceService,
            MultipleChoiceQuestionService multipleChoiceService,
            ObjectMapper objectMapper) {
        this.trueFalseService = trueFalseService;
        this.singleChoiceService = singleChoiceService;
        this.multipleChoiceService = multipleChoiceService;
        this.objectMapper = objectMapper;
    }

    public ImportResult importQuestions(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("El archivo no tiene nombre");
        }

        if (filename.endsWith(".json")) {
            return importFromJson(file);
        } else if (filename.endsWith(".csv")) {
            return importFromCsv(file);
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado. Use JSON o CSV");
        }
    }

    private ImportResult importFromJson(MultipartFile file) throws Exception {
        JsonNode root = objectMapper.readTree(file.getInputStream());
        ImportResult result = new ImportResult();

        if (root.has("trueFalse")) {
            JsonNode tfArray = root.get("trueFalse");
            for (JsonNode node : tfArray) {
                try {
                    TrueFalseQuestion question = new TrueFalseQuestion();
                    question.setStatement(node.get("statement").asText());
                    question.setCorrectAnswer(node.get("correctAnswer").asBoolean());
                    if (node.has("explanation")) {
                        question.setExplanation(node.get("explanation").asText());
                    }
                    trueFalseService.create(question);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.incrementError();
                    result.addError("Error en pregunta V/F: " + e.getMessage());
                }
            }
        }

        if (root.has("singleChoice")) {
            JsonNode scArray = root.get("singleChoice");
            for (JsonNode node : scArray) {
                try {
                    SingleChoiceQuestion question = new SingleChoiceQuestion();
                    question.setStatement(node.get("statement").asText());

                    List<String> options = new ArrayList<>();
                    JsonNode optionsNode = node.get("options");
                    for (JsonNode option : optionsNode) {
                        options.add(option.asText());
                    }
                    question.setOptions(options);
                    question.setCorrectIndex(node.get("correctIndex").asInt());

                    if (node.has("explanation")) {
                        question.setExplanation(node.get("explanation").asText());
                    }
                    singleChoiceService.create(question);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.incrementError();
                    result.addError("Error en pregunta de selección única: " + e.getMessage());
                }
            }
        }

        if (root.has("multipleChoice")) {
            JsonNode mcArray = root.get("multipleChoice");
            for (JsonNode node : mcArray) {
                try {
                    MultipleChoiceQuestion question = new MultipleChoiceQuestion();
                    question.setStatement(node.get("statement").asText());

                    List<String> options = new ArrayList<>();
                    JsonNode optionsNode = node.get("options");
                    for (JsonNode option : optionsNode) {
                        options.add(option.asText());
                    }
                    question.setOptions(options);

                    List<Integer> correctIndexes = new ArrayList<>();
                    JsonNode correctNode = node.get("correctIndexes");
                    for (JsonNode index : correctNode) {
                        correctIndexes.add(index.asInt());
                    }
                    question.setCorrectIndexes(correctIndexes);

                    if (node.has("explanation")) {
                        question.setExplanation(node.get("explanation").asText());
                    }
                    multipleChoiceService.create(question);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.incrementError();
                    result.addError("Error en pregunta de selección múltiple: " + e.getMessage());
                }
            }
        }

        return result;
    }

    private ImportResult importFromCsv(MultipartFile file) throws Exception {
        ImportResult result = new ImportResult();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }

                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length < 3) {
                        result.incrementError();
                        result.addError("Línea inválida: " + line);
                        continue;
                    }

                    String type = parts[0].trim();
                    String statement = parts[1].trim();

                    if ("VF".equalsIgnoreCase(type) || "TF".equalsIgnoreCase(type)) {
                        TrueFalseQuestion question = new TrueFalseQuestion();
                        question.setStatement(statement);
                        question.setCorrectAnswer(Boolean.parseBoolean(parts[2].trim()));
                        if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                            question.setExplanation(parts[3].trim());
                        }
                        trueFalseService.create(question);
                        result.incrementSuccess();

                    } else if ("SC".equalsIgnoreCase(type)) {
                        SingleChoiceQuestion question = new SingleChoiceQuestion();
                        question.setStatement(statement);

                        String[] optionsParts = parts[2].split(";");
                        question.setOptions(Arrays.asList(optionsParts));
                        question.setCorrectIndex(Integer.parseInt(parts[3].trim()));

                        if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                            question.setExplanation(parts[4].trim());
                        }
                        singleChoiceService.create(question);
                        result.incrementSuccess();

                    } else if ("MC".equalsIgnoreCase(type)) {
                        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
                        question.setStatement(statement);

                        String[] optionsParts = parts[2].split(";");
                        question.setOptions(Arrays.asList(optionsParts));

                        String[] indexesParts = parts[3].split(";");
                        List<Integer> indexes = new ArrayList<>();
                        for (String idx : indexesParts) {
                            indexes.add(Integer.parseInt(idx.trim()));
                        }
                        question.setCorrectIndexes(indexes);

                        if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                            question.setExplanation(parts[4].trim());
                        }
                        multipleChoiceService.create(question);
                        result.incrementSuccess();
                    }

                } catch (Exception e) {
                    result.incrementError();
                    result.addError("Error procesando línea: " + e.getMessage());
                }
            }
        }

        return result;
    }

    public static class ImportResult {
        private int successCount = 0;
        private int errorCount = 0;
        private final List<String> errors = new ArrayList<>();

        public void incrementSuccess() {
            successCount++;
        }

        public void incrementError() {
            errorCount++;
        }

        public void addError(String error) {
            errors.add(error);
        }

        public int getSuccessCount() {
            return successCount;
        }

        public int getErrorCount() {
            return errorCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public int getTotalCount() {
            return successCount + errorCount;
        }
    }
}

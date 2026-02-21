package com.app.preguntas.funcionalidades.seleccion_unica.service;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SeleccionUnicaQuestionImportService {

    private final SeleccionUnicaServicioPreguntas service;
    private final ObjectMapper objectMapper;

    public SeleccionUnicaQuestionImportService(SeleccionUnicaServicioPreguntas service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    public SeleccionUnicaQuestionImportResult importFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new SeleccionUnicaQuestionImportResult(0, Collections.emptyList(),
                List.of("Debes seleccionar un archivo valido."));
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        String extension = getExtension(filename).toLowerCase(Locale.ROOT);
        try {
            if ("json".equals(extension)) {
                return importJson(file);
            }
            if ("csv".equals(extension)) {
                return importCsv(file);
            }
            return new SeleccionUnicaQuestionImportResult(0, Collections.emptyList(),
                List.of("Formato no soportado. Usa archivos .json o .csv."));
        } catch (IOException ex) {
            return new SeleccionUnicaQuestionImportResult(0, Collections.emptyList(),
                List.of("No se pudo leer el archivo."));
        }
    }

    private SeleccionUnicaQuestionImportResult importJson(MultipartFile file) throws IOException {
        List<PreguntaSeleccionUnica> input = objectMapper.readValue(
            file.getInputStream(),
            new TypeReference<List<PreguntaSeleccionUnica>>() {}
        );
        return createQuestions(input);
    }

    private SeleccionUnicaQuestionImportResult importCsv(MultipartFile file) throws IOException {
        List<PreguntaSeleccionUnica> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int total = 0;
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            Map<String, Integer> header = null;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                List<String> values = parseCsvLine(line);
                if (values.isEmpty()) {
                    continue;
                }
                if (header == null && looksLikeHeader(values)) {
                    header = buildHeader(values);
                    continue;
                }
                total++;
                PreguntaSeleccionUnica question = parseCsvQuestion(values, header, lineNumber, errors);
                if (question == null) {
                    continue;
                }
                String error = SeleccionUnicaQuestionValidador.validate(question);
                if (error != null) {
                    errors.add("Linea " + lineNumber + ": " + error);
                    continue;
                }
                created.add(service.create(question));
            }
        }
        return new SeleccionUnicaQuestionImportResult(total, created, errors);
    }

    private SeleccionUnicaQuestionImportResult createQuestions(List<PreguntaSeleccionUnica> input) {
        List<PreguntaSeleccionUnica> created = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int index = 0;
        if (input == null) {
            return new SeleccionUnicaQuestionImportResult(0, created, List.of("El archivo JSON debe contener una lista."));
        }
        for (PreguntaSeleccionUnica question : input) {
            index++;
            PreguntaSeleccionUnica normalized = normalize(question);
            String error = SeleccionUnicaQuestionValidador.validate(normalized);
            if (error != null) {
                errors.add("Registro " + index + ": " + error);
                continue;
            }
            created.add(service.create(normalized));
        }
        return new SeleccionUnicaQuestionImportResult(input.size(), created, errors);
    }

    private PreguntaSeleccionUnica normalize(PreguntaSeleccionUnica question) {
        if (question == null) {
            return new PreguntaSeleccionUnica();
        }
        PreguntaSeleccionUnica normalized = new PreguntaSeleccionUnica();
        normalized.setStatement(trimToNull(question.getStatement()));
        normalized.setExplanation(trimToNull(question.getExplanation()));
        normalized.setCorrectIndex(question.getCorrectIndex());
        List<String> sourceOptions = question.getOptions();
        int optionCount = sourceOptions != null ? sourceOptions.size() : 0;
        List<String> options = new ArrayList<>(optionCount);
        if (sourceOptions != null) {
            for (String option : sourceOptions) {
                options.add(trimToNull(option));
            }
        }
        normalized.setOptions(options);
        return normalized;
    }

    private PreguntaSeleccionUnica parseCsvQuestion(List<String> values,
                                                  Map<String, Integer> header,
                                                  int lineNumber,
                                                  List<String> errors) {
        String statement = valueFor(values, header, "statement", "enunciado", 0);
        String optionsRaw = valueFor(values, header, "options", "opciones", 1);
        String correctRaw = valueFor(values, header, "correctindex", "correcta", 2);
        String explanation = valueFor(values, header, "explanation", "explicacion", 3);

        if (statement == null || statement.trim().isEmpty()) {
            errors.add("Linea " + lineNumber + ": La pregunta es obligatoria.");
            return null;
        }
        List<String> options = new ArrayList<>();
        if (optionsRaw != null) {
            for (String option : optionsRaw.split("\\|")) {
                options.add(option.trim());
            }
        }
        Integer correctIndex = parseCorrectIndex(correctRaw, options.size());
        PreguntaSeleccionUnica question = new PreguntaSeleccionUnica();
        question.setStatement(statement.trim());
        question.setOptions(options);
        question.setCorrectIndex(correctIndex);
        question.setExplanation(trimToNull(explanation));
        return question;
    }

    private Integer parseCorrectIndex(String raw, int optionsCount) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            if (optionsCount <= 0) {
                return value;
            }
            if (value >= 1 && value <= optionsCount) {
                return value - 1;
            }
            if (value >= 0 && value < optionsCount) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            return null;
        }
        return null;
    }

    private String valueFor(List<String> values, Map<String, Integer> header, String key, String altKey, int fallback) {
        if (header != null) {
            Integer index = header.get(key);
            if (index == null && altKey != null) {
                index = header.get(altKey);
            }
            if (index == null || index >= values.size()) {
                return null;
            }
            return values.get(index);
        }
        if (fallback >= values.size()) {
            return null;
        }
        return values.get(fallback);
    }

    private boolean looksLikeHeader(List<String> values) {
        for (String value : values) {
            String normalized = normalizeHeader(value);
            if ("statement".equals(normalized) || "enunciado".equals(normalized)
                || "options".equals(normalized) || "opciones".equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Integer> buildHeader(List<String> values) {
        Map<String, Integer> header = new HashMap<>();
        for (int i = 0; i < values.size(); i++) {
            String key = normalizeHeader(values.get(i));
            if (!key.isEmpty()) {
                header.put(key, i);
            }
        }
        return header;
    }

    private String normalizeHeader(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT).replace(" ", "");
    }

    private List<String> parseCsvLine(String line) {
        char delimiter = detectDelimiter(line);
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }
            if (ch == delimiter && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        values.add(current.toString().trim());
        return values;
    }

    private char detectDelimiter(String line) {
        int commas = 0;
        int semicolons = 0;
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (ch == ',') {
                    commas++;
                } else if (ch == ';') {
                    semicolons++;
                }
            }
        }
        return semicolons > commas ? ';' : ',';
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}





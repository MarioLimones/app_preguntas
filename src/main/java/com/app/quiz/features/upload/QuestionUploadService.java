package com.app.quiz.features.upload;

import com.app.quiz.features.mc.model.MultipleChoiceQuestion;
import com.app.quiz.features.mc.service.MultipleChoiceQuestionService;
import com.app.quiz.features.sc.model.SingleChoiceQuestion;
import com.app.quiz.features.sc.service.SingleChoiceQuestionService;
import com.app.quiz.features.vf.model.TrueFalseQuestion;
import com.app.quiz.features.vf.service.TrueFalseQuestionService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionUploadService {

    private final TrueFalseQuestionService vfService;
    private final SingleChoiceQuestionService scService;
    private final MultipleChoiceQuestionService mcService;
    private final ObjectMapper objectMapper;

    public QuestionUploadService(TrueFalseQuestionService vfService,
            SingleChoiceQuestionService scService,
            MultipleChoiceQuestionService mcService,
            ObjectMapper objectMapper) {
        this.vfService = vfService;
        this.scService = scService;
        this.mcService = mcService;
        this.objectMapper = objectMapper;
    }

    public QuestionUploadResult importQuestions(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".json")) {
            return importFromJson(file);
        } else if (filename != null && filename.toLowerCase().endsWith(".csv")) {
            return importFromCsv(file);
        }
        throw new IllegalArgumentException("Formato de archivo no soportado. Use .json o .csv");
    }

    private QuestionUploadResult importFromJson(MultipartFile file) throws IOException {
        int count = 0;
        List<String> errors = new ArrayList<>();

        ImportData data = objectMapper.readValue(file.getInputStream(), ImportData.class);

        if (data.trueFalse != null) {
            for (TrueFalseQuestion q : data.trueFalse) {
                try {
                    vfService.create(q);
                    count++;
                } catch (Exception e) {
                    errors.add("Error en V/F: " + q.getStatement() + " - " + e.getMessage());
                }
            }
        }

        if (data.singleChoice != null) {
            for (SingleChoiceQuestion q : data.singleChoice) {
                try {
                    scService.create(q);
                    count++;
                } catch (Exception e) {
                    errors.add("Error en SC: " + q.getStatement() + " - " + e.getMessage());
                }
            }
        }

        if (data.multipleChoice != null) {
            for (MultipleChoiceQuestion q : data.multipleChoice) {
                try {
                    mcService.create(q);
                    count++;
                } catch (Exception e) {
                    errors.add("Error en MC: " + q.getStatement() + " - " + e.getMessage());
                }
            }
        }

        return new QuestionUploadResult(count, errors);
    }

    private QuestionUploadResult importFromCsv(MultipartFile file) throws IOException {
        int count = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().contains("tipo") || line.toLowerCase().contains("enunciado"))
                        continue;
                }

                try {
                    String[] parts = line.split(",", -1);
                    if (parts.length < 4) {
                        errors.add("Línea mal formateada: " + line);
                        continue;
                    }

                    String type = parts[0].trim().toUpperCase();
                    String statement = parts[1].trim();
                    String dataRaw = parts[2].trim();
                    String answerRaw = parts[3].trim();
                    String explanation = parts.length > 4 ? parts[4].trim() : "";

                    if (type.equals("VF")) {
                        TrueFalseQuestion q = new TrueFalseQuestion();
                        q.setStatement(statement);
                        q.setCorrectAnswer(Boolean.parseBoolean(dataRaw));
                        q.setExplanation(explanation);
                        vfService.create(q);
                        count++;
                    } else if (type.equals("SC")) {
                        SingleChoiceQuestion q = new SingleChoiceQuestion();
                        q.setStatement(statement);
                        q.setOptions(Arrays.asList(dataRaw.split(";")));
                        q.setCorrectIndex(Integer.parseInt(answerRaw));
                        q.setExplanation(explanation);
                        scService.create(q);
                        count++;
                    } else if (type.equals("MC")) {
                        MultipleChoiceQuestion q = new MultipleChoiceQuestion();
                        q.setStatement(statement);
                        q.setOptions(Arrays.asList(dataRaw.split(";")));
                        q.setCorrectIndexes(Arrays.stream(answerRaw.split(";"))
                                .map(String::trim)
                                .map(Integer::parseInt)
                                .collect(Collectors.toList()));
                        q.setExplanation(explanation);
                        mcService.create(q);
                        count++;
                    } else {
                        errors.add("Tipo desconocido '" + type + "' en línea: " + line);
                    }
                } catch (Exception e) {
                    errors.add("Error procesando línea: " + line + " - " + e.getMessage());
                }
            }
        }

        return new QuestionUploadResult(count, errors);
    }

    private static class ImportData {
        public List<TrueFalseQuestion> trueFalse;
        public List<SingleChoiceQuestion> singleChoice;
        public List<MultipleChoiceQuestion> multipleChoice;
    }
}

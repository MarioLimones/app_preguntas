package com.app.preguntas.funcionalidades.upload;

import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.SeleccionMultipleServicioPreguntas;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
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

    private final VerdaderoFalsoServicioPreguntas vfService;
    private final SeleccionUnicaServicioPreguntas scService;
    private final SeleccionMultipleServicioPreguntas mcService;
    private final ObjectMapper objectMapper;

    public QuestionUploadService(VerdaderoFalsoServicioPreguntas vfService,
            SeleccionUnicaServicioPreguntas scService,
            SeleccionMultipleServicioPreguntas mcService,
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

        if (data.VerdaderoFalso != null) {
            for (PreguntaVerdaderoFalso q : data.VerdaderoFalso) {
                try {
                    vfService.create(q);
                    count++;
                } catch (Exception e) {
                    errors.add("Error en V/F: " + q.getStatement() + " - " + e.getMessage());
                }
            }
        }

        if (data.SeleccionUnica != null) {
            for (PreguntaSeleccionUnica q : data.SeleccionUnica) {
                try {
                    scService.create(q);
                    count++;
                } catch (Exception e) {
                    errors.add("Error en SC: " + q.getStatement() + " - " + e.getMessage());
                }
            }
        }

        if (data.SeleccionMultiple != null) {
            for (PreguntaSeleccionMultiple q : data.SeleccionMultiple) {
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
                        PreguntaVerdaderoFalso q = new PreguntaVerdaderoFalso();
                        q.setStatement(statement);
                        q.setCorrectAnswer(Boolean.parseBoolean(dataRaw));
                        q.setExplanation(explanation);
                        vfService.create(q);
                        count++;
                    } else if (type.equals("SC")) {
                        PreguntaSeleccionUnica q = new PreguntaSeleccionUnica();
                        q.setStatement(statement);
                        q.setOptions(Arrays.asList(dataRaw.split(";")));
                        q.setCorrectIndex(Integer.parseInt(answerRaw));
                        q.setExplanation(explanation);
                        scService.create(q);
                        count++;
                    } else if (type.equals("MC")) {
                        PreguntaSeleccionMultiple q = new PreguntaSeleccionMultiple();
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
        public List<PreguntaVerdaderoFalso> VerdaderoFalso;
        public List<PreguntaSeleccionUnica> SeleccionUnica;
        public List<PreguntaSeleccionMultiple> SeleccionMultiple;
    }
}


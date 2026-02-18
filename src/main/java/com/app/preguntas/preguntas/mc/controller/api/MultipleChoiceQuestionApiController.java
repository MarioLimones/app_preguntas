package com.app.preguntas.preguntas.mc.controller.api;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
import com.app.preguntas.preguntas.mc.service.MultipleChoiceQuestionService;
import com.app.preguntas.preguntas.mc.service.OpenTdbQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/mc/questions")
@Tag(name = "Selección Múltiple", description = "CRUD e importación de preguntas de Selección Múltiple")
public class MultipleChoiceQuestionApiController {

    private final MultipleChoiceQuestionService service;
    private final OpenTdbQuestionService openTdbQuestionService;

    public MultipleChoiceQuestionApiController(MultipleChoiceQuestionService service,
            OpenTdbQuestionService openTdbQuestionService) {
        this.service = service;
        this.openTdbQuestionService = openTdbQuestionService;
    }

    @GetMapping
    public List<MultipleChoiceQuestion> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MultipleChoiceQuestion> get(@PathVariable Long id) {
        Optional<MultipleChoiceQuestion> question = service.findById(id);
        return question.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MultipleChoiceQuestion question) {
        String error = validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        MultipleChoiceQuestion created = service.create(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody MultipleChoiceQuestion question) {
        String error = validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<MultipleChoiceQuestion> updated = service.update(id, question);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/random")
    public ResponseEntity<MultipleChoiceQuestion> random() {
        return service.getRandom()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/import")
    public ResponseEntity<List<MultipleChoiceQuestion>> importFromOpenTdb(
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String difficulty) {
        List<MultipleChoiceQuestion> imported = openTdbQuestionService.fetchMultipleChoiceQuestions(amount, category,
                difficulty);
        List<MultipleChoiceQuestion> created = imported.stream()
                .map(service::create)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private String validate(MultipleChoiceQuestion question) {
        if (question.getStatement() == null || question.getStatement().trim().isEmpty()) {
            return "La pregunta es obligatoria.";
        }
        if (question.getOptions() == null || question.getOptions().size() < 2) {
            return "Debes ingresar al menos dos opciones.";
        }
        Set<String> seen = new HashSet<>();
        for (String option : question.getOptions()) {
            if (option == null || option.trim().isEmpty()) {
                return "No se permiten opciones vacias.";
            }
            String normalized = option.trim().toLowerCase();
            if (!seen.add(normalized)) {
                return "No se permiten opciones duplicadas.";
            }
        }
        if (question.getCorrectIndexes() == null || question.getCorrectIndexes().isEmpty()) {
            return "Debes indicar al menos una opcion correcta.";
        }
        Set<Integer> indexes = new HashSet<>();
        for (Integer index : question.getCorrectIndexes()) {
            if (index == null) {
                return "Las opciones correctas no son validas.";
            }
            if (index < 0 || index >= question.getOptions().size()) {
                return "Las opciones correctas son invalidas.";
            }
            if (!indexes.add(index)) {
                return "No se permiten opciones correctas duplicadas.";
            }
        }
        return null;
    }
}

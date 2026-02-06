package com.app.preguntas.preguntas.sc.controller.api;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/sc/questions")
public class SingleChoiceQuestionApiController {

    private final SingleChoiceQuestionService service;

    public SingleChoiceQuestionApiController(SingleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SingleChoiceQuestion> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleChoiceQuestion> get(@PathVariable Long id) {
        Optional<SingleChoiceQuestion> question = service.findById(id);
        return question.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SingleChoiceQuestion question) {
        String error = validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        SingleChoiceQuestion created = service.create(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SingleChoiceQuestion question) {
        String error = validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<SingleChoiceQuestion> updated = service.update(id, question);
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
    public ResponseEntity<SingleChoiceQuestion> random() {
        return service.getRandom()
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String validate(SingleChoiceQuestion question) {
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
        if (question.getCorrectIndex() == null) {
            return "Debes indicar la opcion correcta.";
        }
        if (question.getCorrectIndex() < 0 || question.getCorrectIndex() >= question.getOptions().size()) {
            return "La opcion correcta es invalida.";
        }
        return null;
    }
}

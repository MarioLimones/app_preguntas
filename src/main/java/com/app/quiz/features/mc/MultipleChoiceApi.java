package com.app.quiz.features.mc;

import com.app.quiz.features.mc.model.MultipleChoiceQuestion;
import com.app.quiz.features.mc.service.MultipleChoiceQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.quiz.features.mc.service.OpenTdbQuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/mc/questions")
@Tag(name = "Multiple Choice API", description = "Operaciones para preguntas de selección múltiple")
public class MultipleChoiceApi {

    private final MultipleChoiceQuestionService service;
    private final OpenTdbQuestionService openTdbService;

    public MultipleChoiceApi(MultipleChoiceQuestionService service,
            OpenTdbQuestionService openTdbService) {
        this.service = service;
        this.openTdbService = openTdbService;
    }

    @PostMapping("/import")
    @Operation(summary = "Importar preguntas desde OpenTDB")
    public List<MultipleChoiceQuestion> importQuestions(
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String difficulty) {
        List<MultipleChoiceQuestion> questions = openTdbService.fetchMultipleChoiceQuestions(amount, category,
                difficulty);
        for (MultipleChoiceQuestion q : questions) {
            service.create(q);
        }
        return questions;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de selección múltiple")
    public List<MultipleChoiceQuestion> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<MultipleChoiceQuestion> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva pregunta")
    @ResponseStatus(HttpStatus.CREATED)
    public MultipleChoiceQuestion create(@RequestBody MultipleChoiceQuestion question) {
        return service.create(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta existente")
    public ResponseEntity<MultipleChoiceQuestion> update(@PathVariable Long id,
            @RequestBody MultipleChoiceQuestion question) {
        return service.update(id, question)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una pregunta")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

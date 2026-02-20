package com.app.quiz.features.mc;

import com.app.quiz.features.mc.model.MultipleChoiceQuestion;
import com.app.quiz.features.mc.service.MultipleChoiceQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mc/questions")
@Tag(name = "Multiple Choice API", description = "Operaciones para preguntas de selección múltiple")
public class MultipleChoiceApi {

    private final MultipleChoiceQuestionService service;

    public MultipleChoiceApi(MultipleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de selección múltiple")
    public List<MultipleChoiceQuestion> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<MultipleChoiceQuestion> getById(@PathVariable String id) {
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
    public ResponseEntity<MultipleChoiceQuestion> update(@PathVariable String id,
            @RequestBody MultipleChoiceQuestion question) {
        return service.update(id, question)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una pregunta")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

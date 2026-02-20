package com.app.quiz.features.vf;

import com.app.quiz.features.vf.model.TrueFalseQuestion;
import com.app.quiz.features.vf.service.TrueFalseQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vf/questions")
@Tag(name = "True/False API", description = "Operaciones para preguntas de verdadero o falso")
public class TrueFalseApi {

    private final TrueFalseQuestionService service;

    public TrueFalseApi(TrueFalseQuestionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de verdadero o falso")
    public List<TrueFalseQuestion> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<TrueFalseQuestion> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva pregunta")
    @ResponseStatus(HttpStatus.CREATED)
    public TrueFalseQuestion create(@RequestBody TrueFalseQuestion question) {
        return service.save(question); // Note: Service might use save() instead of create()
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta existente")
    public ResponseEntity<TrueFalseQuestion> update(@PathVariable String id, @RequestBody TrueFalseQuestion question) {
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

package com.app.preguntas.preguntas.vf.controller.api;

import com.app.preguntas.preguntas.vf.model.TrueFalseQuestion;
import com.app.preguntas.preguntas.vf.service.TrueFalseQuestionService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vf/questions")
@Tag(name = "Verdadero/Falso", description = "CRUD de preguntas Verdadero/Falso")
public class TrueFalseQuestionApiController {

    private final TrueFalseQuestionService service;

    public TrueFalseQuestionApiController(TrueFalseQuestionService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrueFalseQuestion> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrueFalseQuestion> get(@PathVariable Long id) {
        Optional<TrueFalseQuestion> question = service.findById(id);
        return question.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TrueFalseQuestion> create(@Valid @RequestBody TrueFalseQuestion question) {
        TrueFalseQuestion created = service.create(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrueFalseQuestion> update(@PathVariable Long id,
            @Valid @RequestBody TrueFalseQuestion question) {
        Optional<TrueFalseQuestion> updated = service.update(id, question);
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
    public ResponseEntity<TrueFalseQuestion> random() {
        return service.getRandom()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

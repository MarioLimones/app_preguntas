package com.app.preguntas.preguntas.sc.controller.api;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionImportResult;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionImportService;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionService;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionValidator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sc/questions")
public class SingleChoiceQuestionApiController {

    private final SingleChoiceQuestionService service;
    private final SingleChoiceQuestionImportService importService;

    public SingleChoiceQuestionApiController(SingleChoiceQuestionService service,
                                             SingleChoiceQuestionImportService importService) {
        this.service = service;
        this.importService = importService;
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
        String error = SingleChoiceQuestionValidator.validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        SingleChoiceQuestion created = service.create(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody SingleChoiceQuestion question) {
        String error = SingleChoiceQuestionValidator.validate(question);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<SingleChoiceQuestion> updated = service.update(id, question);
        return updated.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        SingleChoiceQuestionImportResult result = importService.importFile(file);
        if (!result.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(new UploadResponse(result));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponse(result));
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

    private static class UploadResponse {
        private final int total;
        private final int created;
        private final List<String> errors;

        private UploadResponse(SingleChoiceQuestionImportResult result) {
            this.total = result.getTotal();
            this.created = result.getCreatedCount();
            this.errors = result.getErrors();
        }

        public int getTotal() {
            return total;
        }

        public int getCreated() {
            return created;
        }

        public List<String> getErrors() {
            return errors;
        }
    }
}

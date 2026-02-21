package com.app.preguntas.funcionalidades.seleccion_multiple;

import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.SeleccionMultipleServicioPreguntas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.OpenTdbServicioPreguntas;

import java.util.List;

@RestController
@RequestMapping("/api/mc/preguntas")
@Tag(name = "Multiple Choice API", description = "Operaciones para preguntas de selección múltiple")
public class ApiSeleccionMultiple {

    private final SeleccionMultipleServicioPreguntas service;
    private final OpenTdbServicioPreguntas openTdbService;

    public ApiSeleccionMultiple(SeleccionMultipleServicioPreguntas service,
            OpenTdbServicioPreguntas openTdbService) {
        this.service = service;
        this.openTdbService = openTdbService;
    }

    @PostMapping("/import")
    @Operation(summary = "Importar preguntas desde OpenTDB")
    public List<PreguntaSeleccionMultiple> importQuestions(
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(required = false) Integer Categoria,
            @RequestParam(required = false) String difficulty) {
        List<PreguntaSeleccionMultiple> questions = openTdbService.fetchSeleccionMultipleQuestions(amount, Categoria,
                difficulty);
        for (PreguntaSeleccionMultiple q : questions) {
            service.create(q);
        }
        return questions;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de selección múltiple")
    public List<PreguntaSeleccionMultiple> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<PreguntaSeleccionMultiple> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva pregunta")
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaSeleccionMultiple create(@RequestBody PreguntaSeleccionMultiple question) {
        return service.create(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta existente")
    public ResponseEntity<PreguntaSeleccionMultiple> update(@PathVariable Long id,
            @RequestBody PreguntaSeleccionMultiple question) {
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


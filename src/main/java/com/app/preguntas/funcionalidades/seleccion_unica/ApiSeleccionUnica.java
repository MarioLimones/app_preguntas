package com.app.preguntas.funcionalidades.seleccion_unica;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sc/preguntas")
@Tag(name = "Single Choice API", description = "Operaciones para preguntas de selección única")
public class ApiSeleccionUnica {

    private final SeleccionUnicaServicioPreguntas service;

    public ApiSeleccionUnica(SeleccionUnicaServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de selección única")
    public List<PreguntaSeleccionUnica> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<PreguntaSeleccionUnica> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva pregunta")
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaSeleccionUnica create(@RequestBody PreguntaSeleccionUnica question) {
        return service.create(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta existente")
    public ResponseEntity<PreguntaSeleccionUnica> update(@PathVariable Long id,
            @RequestBody PreguntaSeleccionUnica question) {
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


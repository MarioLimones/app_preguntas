package com.app.preguntas.funcionalidades.verdadero_falso;

import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vf/preguntas")
@Tag(name = "True/False API", description = "Operaciones para preguntas de verdadero o falso")
public class ApiVerdaderoFalso {

    private final VerdaderoFalsoServicioPreguntas service;

    public ApiVerdaderoFalso(VerdaderoFalsoServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las preguntas de verdadero o falso")
    public List<PreguntaVerdaderoFalso> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una pregunta por ID")
    public ResponseEntity<PreguntaVerdaderoFalso> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear una nueva pregunta")
    @ResponseStatus(HttpStatus.CREATED)
    public PreguntaVerdaderoFalso create(@RequestBody PreguntaVerdaderoFalso question) {
        return service.create(question);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una pregunta existente")
    public ResponseEntity<PreguntaVerdaderoFalso> update(@PathVariable Long id, @RequestBody PreguntaVerdaderoFalso question) {
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


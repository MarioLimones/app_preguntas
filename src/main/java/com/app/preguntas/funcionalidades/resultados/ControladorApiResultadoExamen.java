package com.app.preguntas.funcionalidades.resultados;

import com.app.preguntas.nucleo.ResultadoExamen;
import com.app.preguntas.funcionalidades.resultados.ServicioResultadoExamen;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@Tag(name = "Resultados de Tests", description = "Almacenamiento y consulta de resultados de tests")
public class ControladorApiResultadoExamen {

    private final ServicioResultadoExamen service;

    public ControladorApiResultadoExamen(ServicioResultadoExamen service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los resultados o filtrar por usuario")
    public List<ResultadoExamen> list(@RequestParam(required = false) String username) {
        if (username != null && !username.isBlank()) {
            return service.findByUsername(username);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un resultado por ID")
    public ResponseEntity<ResultadoExamen> get(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Guardar un resultado de test")
    public ResponseEntity<ResultadoExamen> save(@RequestBody ResultadoExamen result) {
        if (result.getCompletedAt() == null) {
            result.setCompletedAt(LocalDateTime.now());
        }
        ResultadoExamen saved = service.save(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un resultado")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}







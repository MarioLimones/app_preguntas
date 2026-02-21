package com.app.preguntas.funcionalidades.auth;

import com.app.preguntas.nucleo.UsuarioApp;
import com.app.preguntas.funcionalidades.autenticacion.ServicioUsuarioApp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios (MongoDB)")
public class ControladorApiUsuario {

    private final ServicioUsuarioApp service;

    public ControladorApiUsuario(ServicioUsuarioApp service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public List<Map<String, Object>> list() {
        return service.findAll().stream().map(this::safeUser).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID")
    public ResponseEntity<Map<String, Object>> get(@PathVariable String id) {
        return service.findById(id)
                .map(u -> ResponseEntity.ok(safeUser(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    public ResponseEntity<Map<String, Object>> create(@RequestBody UsuarioApp user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (service.findByUsername(user.getUsername()).isPresent()) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", "El nombre de usuario ya existe");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        }
        UsuarioApp created = service.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(safeUser(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String id, @RequestBody UsuarioApp user) {
        return service.update(id, user)
                .map(u -> ResponseEntity.ok(safeUser(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Map<String, Object> safeUser(UsuarioApp user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("role", user.getRole());
        return map;
    }
}




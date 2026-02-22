package com.app.preguntas.funcionalidades.autenticacion;

import com.app.preguntas.nucleo.UsuarioApp;
import com.app.preguntas.funcionalidades.autenticacion.ServicioUsuarioApp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Login y registro de usuarios")
public class ControladorApiAutenticacion {

    private final ServicioUsuarioApp userService;
    private final PasswordEncoder passwordEncoder;

    public ControladorApiAutenticacion(ServicioUsuarioApp userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión con usuario y contraseña")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Se requiere username y password"));
        }

        Optional<UsuarioApp> optUser = userService.findByUsername(username);
        if (optUser.isEmpty() || !passwordEncoder.matches(password, optUser.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        UsuarioApp user = optUser.get();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("message", "Login exitoso");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Se requiere username y password"));
        }

        if (userService.findByUsername(username).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "El nombre de usuario ya existe"));
        }

        UsuarioApp newUser = new UsuarioApp(username, password, "USER");
        UsuarioApp created = userService.create(newUser);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", created.getId());
        response.put("username", created.getUsername());
        response.put("role", created.getRole());
        response.put("message", "Registro exitoso");

        return ResponseEntity.status(201).body(response);
    }
}





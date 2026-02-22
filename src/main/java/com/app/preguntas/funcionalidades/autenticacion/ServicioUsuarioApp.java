package com.app.preguntas.funcionalidades.autenticacion;

import com.app.preguntas.nucleo.UsuarioApp;
import com.app.preguntas.funcionalidades.autenticacion.RepositorioUsuarioApp;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioUsuarioApp {

    private final RepositorioUsuarioApp repository;
    private final PasswordEncoder passwordEncoder;

    public ServicioUsuarioApp(RepositorioUsuarioApp repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultUser() {
        if (!repository.existsByUsername("admin")) {
            UsuarioApp admin = new UsuarioApp("admin", passwordEncoder.encode("1234"), "ADMIN");
            repository.save(admin);
        }
    }

    public List<UsuarioApp> findAll() {
        return repository.findAll();
    }

    public Optional<UsuarioApp> findById(String id) {
        return repository.findById(id);
    }

    public Optional<UsuarioApp> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public UsuarioApp create(UsuarioApp user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        return repository.save(user);
    }

    public Optional<UsuarioApp> update(String id, UsuarioApp input) {
        return repository.findById(id).map(existing -> {
            existing.setUsername(input.getUsername());
            if (input.getPassword() != null && !input.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(input.getPassword()));
            }
            if (input.getRole() != null && !input.getRole().isBlank()) {
                existing.setRole(input.getRole());
            }
            return repository.save(existing);
        });
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public long count() {
        return repository.count();
    }
}








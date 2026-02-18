package com.app.preguntas.preguntas.common.service;

import com.app.preguntas.preguntas.common.model.AppUser;
import com.app.preguntas.preguntas.common.repository.AppUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultUser() {
        if (!repository.existsByUsername("admin")) {
            AppUser admin = new AppUser("admin", passwordEncoder.encode("1234"), "ADMIN");
            repository.save(admin);
        }
    }

    public List<AppUser> findAll() {
        return repository.findAll();
    }

    public Optional<AppUser> findById(String id) {
        return repository.findById(id);
    }

    public Optional<AppUser> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    public AppUser create(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        return repository.save(user);
    }

    public Optional<AppUser> update(String id, AppUser input) {
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

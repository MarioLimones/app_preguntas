package com.app.preguntas.funcionalidades.autenticacion;

import com.app.preguntas.nucleo.UsuarioApp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuarioApp extends MongoRepository<UsuarioApp, String> {
    Optional<UsuarioApp> findByUsername(String username);

    boolean existsByUsername(String username);
}







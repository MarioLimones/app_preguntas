package com.app.preguntas.nucleo;

import com.app.preguntas.nucleo.Categoria;
import org.springframework.data.mongodb.repositorio.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioCategoria extends MongoRepository<Categoria, String> {
    Optional<Categoria> findByName(String name);
}




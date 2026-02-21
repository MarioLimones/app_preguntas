package com.app.preguntas.nucleo;

import com.app.preguntas.nucleo.Categoria;
import com.app.preguntas.nucleo.RepositorioCategoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioCategoria {

    private final RepositorioCategoria repository;

    public ServicioCategoria(RepositorioCategoria repository) {
        this.repository = repository;
    }

    public List<Categoria> findAll() {
        return repository.findAll();
    }

    public Optional<Categoria> findById(String id) {
        return repository.findById(id);
    }

    public Categoria create(Categoria Categoria) {
        return repository.save(Categoria);
    }

    public Optional<Categoria> update(String id, Categoria Categoria) {
        if (repository.existsById(id)) {
            Categoria.setId(id);
            return Optional.of(repository.save(Categoria));
        }
        return Optional.empty();
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}




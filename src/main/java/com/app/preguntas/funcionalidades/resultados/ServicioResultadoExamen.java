package com.app.preguntas.funcionalidades.resultados;

import com.app.preguntas.nucleo.ResultadoExamen;
import com.app.preguntas.funcionalidades.resultados.RepositorioResultadoExamen;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioResultadoExamen {

    private final RepositorioResultadoExamen repository;

    public ServicioResultadoExamen(RepositorioResultadoExamen repository) {
        this.repository = repository;
    }

    public ResultadoExamen save(ResultadoExamen result) {
        return repository.save(result);
    }

    public List<ResultadoExamen> findByUsername(String username) {
        return repository.findByUsernameOrderByCompletedAtDesc(username);
    }

    public List<ResultadoExamen> findAll() {
        return repository.findAll();
    }

    public Optional<ResultadoExamen> findById(String id) {
        return repository.findById(id);
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}








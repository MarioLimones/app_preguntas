package com.app.preguntas.funcionalidades.results;

import com.app.preguntas.nucleo.ResultadoExamen;
import org.springframework.data.mongodb.repositorio.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositorioResultadoExamen extends MongoRepository<ResultadoExamen, String> {
    List<ResultadoExamen> findByUsernameOrderByCompletedAtDesc(String username);
}




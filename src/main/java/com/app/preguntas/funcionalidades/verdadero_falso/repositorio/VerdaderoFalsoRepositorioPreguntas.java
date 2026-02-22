package com.app.preguntas.funcionalidades.verdadero_falso.repositorio;

import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerdaderoFalsoRepositorioPreguntas extends JpaRepository<PreguntaVerdaderoFalso, Long> {
    Optional<PreguntaVerdaderoFalso> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}




package com.app.preguntas.funcionalidades.seleccion_multiple.repositorio;

import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SeleccionMultipleRepositorioPreguntas extends JpaRepository<PreguntaSeleccionMultiple, Long> {
    Optional<PreguntaSeleccionMultiple> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}

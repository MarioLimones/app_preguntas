package com.app.preguntas.funcionalidades.seleccion_multiple.repositorio;

import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

@Repository
public interface SeleccionMultipleRepositorioPreguntas extends JpaRepository<PreguntaSeleccionMultiple, Long> {
    @Override
    @EntityGraph(attributePaths = {"options", "correctIndexes"})
    List<PreguntaSeleccionMultiple> findAll();

    @Override
    @EntityGraph(attributePaths = {"options", "correctIndexes"})
    Page<PreguntaSeleccionMultiple> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"options", "correctIndexes"})
    Optional<PreguntaSeleccionMultiple> findById(Long id);

    @EntityGraph(attributePaths = {"options", "correctIndexes"})
    Optional<PreguntaSeleccionMultiple> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}




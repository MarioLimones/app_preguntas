package com.app.preguntas.funcionalidades.seleccion_unica.repositorio;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

@Repository
public interface SeleccionUnicaRepositorioPreguntas extends JpaRepository<PreguntaSeleccionUnica, Long> {
    @Override
    @EntityGraph(attributePaths = "options")
    List<PreguntaSeleccionUnica> findAll();

    @Override
    @EntityGraph(attributePaths = "options")
    Page<PreguntaSeleccionUnica> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "options")
    Optional<PreguntaSeleccionUnica> findById(Long id);

    @EntityGraph(attributePaths = "options")
    Optional<PreguntaSeleccionUnica> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}




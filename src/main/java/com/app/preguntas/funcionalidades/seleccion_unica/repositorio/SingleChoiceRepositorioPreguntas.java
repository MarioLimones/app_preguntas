package com.app.preguntas.funcionalidades.seleccion_unica.repositorio;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SeleccionUnicaRepositorioPreguntas extends JpaRepository<PreguntaSeleccionUnica, Long> {
    Optional<PreguntaSeleccionUnica> findFirstByIdGreaterThanOrderByIdAsc(Long id);
}

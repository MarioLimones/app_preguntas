package com.app.preguntas.funcionalidades.seleccion_unica.servicio;

import com.app.preguntas.nucleo.ResultadoPagina;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.repositorio.SeleccionUnicaRepositorioPreguntas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SeleccionUnicaServicioPreguntas {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final SeleccionUnicaRepositorioPreguntas repository;

    public SeleccionUnicaServicioPreguntas(SeleccionUnicaRepositorioPreguntas repository) {
        this.repository = repository;
    }

    public ResultadoPagina<PreguntaSeleccionUnica> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        Page<PreguntaSeleccionUnica> springPage = repository.findAll(PageRequest.of(page, pageSize, Sort.by("id")));
        return new ResultadoPagina<>(springPage.getContent(), springPage.getTotalElements(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<PreguntaSeleccionUnica> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<PreguntaSeleccionUnica> findById(Long id) {
        return repository.findById(id);
    }

    public PreguntaSeleccionUnica create(PreguntaSeleccionUnica input) {
        return repository.save(input);
    }

    public Optional<PreguntaSeleccionUnica> update(Long id, PreguntaSeleccionUnica input) {
        if (id == null || !repository.existsById(id)) {
            return Optional.empty();
        }
        input.setId(id);
        return Optional.of(repository.save(input));
    }

    public boolean delete(Long id) {
        if (id != null && repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<PreguntaSeleccionUnica> getRandom() {
        long total = repository.count();
        if (total == 0) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt((int) total);
        Page<PreguntaSeleccionUnica> page = repository.findAll(PageRequest.of(randomIndex, 1));
        return page.hasContent() ? Optional.of(page.getContent().get(0)) : Optional.empty();
    }

    public Optional<PreguntaSeleccionUnica> getNext(Long currentId) {
        if (currentId == null) {
            Page<PreguntaSeleccionUnica> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
            return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
        }

        return repository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .or(() -> {
                    Page<PreguntaSeleccionUnica> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
                    return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
                });
    }

    public List<Long> getAllIdsSorted() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(PreguntaSeleccionUnica::getId)
                .toList();
    }
}

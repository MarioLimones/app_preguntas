package com.app.preguntas.funcionalidades.seleccion_multiple.servicio;

import com.app.preguntas.nucleo.ResultadoPagina;
import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import com.app.preguntas.funcionalidades.seleccion_multiple.repositorio.SeleccionMultipleRepositorioPreguntas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SeleccionMultipleServicioPreguntas {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final SeleccionMultipleRepositorioPreguntas repository;

    public SeleccionMultipleServicioPreguntas(SeleccionMultipleRepositorioPreguntas repository) {
        this.repository = repository;
    }

    public ResultadoPagina<PreguntaSeleccionMultiple> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        Page<PreguntaSeleccionMultiple> springPage = repository.findAll(PageRequest.of(page, pageSize, Sort.by("id")));
        return new ResultadoPagina<>(springPage.getContent(), springPage.getTotalElements(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<PreguntaSeleccionMultiple> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<PreguntaSeleccionMultiple> findById(Long id) {
        return repository.findById(id);
    }

    public PreguntaSeleccionMultiple create(PreguntaSeleccionMultiple input) {
        return repository.save(input);
    }

    public Optional<PreguntaSeleccionMultiple> update(Long id, PreguntaSeleccionMultiple input) {
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

    public Optional<PreguntaSeleccionMultiple> getRandom() {
        long total = repository.count();
        if (total == 0) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt((int) total);
        Page<PreguntaSeleccionMultiple> page = repository.findAll(PageRequest.of(randomIndex, 1));
        return page.hasContent() ? Optional.of(page.getContent().get(0)) : Optional.empty();
    }

    public Optional<PreguntaSeleccionMultiple> getNext(Long currentId) {
        if (currentId == null) {
            Page<PreguntaSeleccionMultiple> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
            return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
        }

        return repository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .or(() -> {
                    Page<PreguntaSeleccionMultiple> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
                    return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
                });
    }

    public List<Long> getAllIdsSorted() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(PreguntaSeleccionMultiple::getId)
                .toList();
    }
}





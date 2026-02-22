package com.app.preguntas.funcionalidades.verdadero_falso.servicio;

import com.app.preguntas.nucleo.ResultadoPagina;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.repositorio.VerdaderoFalsoRepositorioPreguntas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerdaderoFalsoServicioPreguntas {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final VerdaderoFalsoRepositorioPreguntas repository;

    public VerdaderoFalsoServicioPreguntas(VerdaderoFalsoRepositorioPreguntas repository) {
        this.repository = repository;
    }

    public ResultadoPagina<PreguntaVerdaderoFalso> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        Page<PreguntaVerdaderoFalso> springPage = repository.findAll(PageRequest.of(page, pageSize, Sort.by("id")));
        return new ResultadoPagina<>(springPage.getContent(), (int) springPage.getTotalElements(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<PreguntaVerdaderoFalso> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<PreguntaVerdaderoFalso> findById(Long id) {
        return repository.findById(id);
    }

    public PreguntaVerdaderoFalso create(PreguntaVerdaderoFalso input) {
        return repository.save(input);
    }

    public Optional<PreguntaVerdaderoFalso> update(Long id, PreguntaVerdaderoFalso input) {
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

    public Optional<PreguntaVerdaderoFalso> getRandom() {
        long total = repository.count();
        if (total == 0) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt((int) total);
        Page<PreguntaVerdaderoFalso> page = repository.findAll(PageRequest.of(randomIndex, 1));
        return page.hasContent() ? Optional.of(page.getContent().get(0)) : Optional.empty();
    }

    public Optional<PreguntaVerdaderoFalso> getNext(Long currentId) {
        if (currentId == null) {
            Page<PreguntaVerdaderoFalso> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
            return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
        }

        // Find the first one with ID > currentId
        return repository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .or(() -> {
                    // If none found, wrap around to the first one
                    Page<PreguntaVerdaderoFalso> first = repository.findAll(PageRequest.of(0, 1, Sort.by("id")));
                    return first.hasContent() ? Optional.of(first.getContent().get(0)) : Optional.empty();
                });
    }

    public List<Long> getAllIdsSorted() {
        // This is still potentially memory intensive, but if needed for small sets it's
        // okay.
        // For true optimization, we should use a projection to only fetch IDs.
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(PreguntaVerdaderoFalso::getId)
                .toList();
    }
}





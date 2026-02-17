package com.app.preguntas.preguntas.vf.service;

import com.app.preguntas.preguntas.common.model.PageResult;
import com.app.preguntas.preguntas.vf.model.TrueFalseQuestion;
import com.app.preguntas.preguntas.vf.repository.TrueFalseQuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class TrueFalseQuestionService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final TrueFalseQuestionRepository repository;

    public TrueFalseQuestionService(TrueFalseQuestionRepository repository) {
        this.repository = repository;
    }

    public PageResult<TrueFalseQuestion> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        return new PageResult<>(findAll(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<TrueFalseQuestion> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<TrueFalseQuestion> findById(Long id) {
        return repository.findById(id);
    }

    public TrueFalseQuestion create(TrueFalseQuestion input) {
        // JPA handles ID generation
        return repository.save(input);
    }

    public Optional<TrueFalseQuestion> update(Long id, TrueFalseQuestion input) {
        if (id == null || !repository.existsById(id)) {
            return Optional.empty();
        }
        input.setId(id);
        return Optional.of(repository.save(input));
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<TrueFalseQuestion> getRandom() {
        List<TrueFalseQuestion> items = findAll();
        int size = items.size();
        if (size == 0) {
            return Optional.empty();
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return Optional.of(items.get(index));
    }

    public Optional<TrueFalseQuestion> getNext(Long currentId) {
        List<TrueFalseQuestion> items = findAll();
        int size = items.size();
        if (size == 0) {
            return Optional.empty();
        }
        if (currentId == null) {
            return Optional.of(items.get(0));
        }
        for (int i = 0; i < size; i++) {
            if (items.get(i).getId().equals(currentId)) {
                return Optional.of(items.get(Math.min(i + 1, size - 1)));
            }
        }
        return Optional.of(items.get(0));
    }

    public List<Long> getAllIdsSorted() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(TrueFalseQuestion::getId)
                .toList();
    }
}

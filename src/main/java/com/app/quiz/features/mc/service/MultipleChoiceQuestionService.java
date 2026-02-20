package com.app.quiz.features.mc.service;

import com.app.quiz.core.PageResult;
import com.app.quiz.features.mc.model.MultipleChoiceQuestion;
import com.app.quiz.features.mc.repository.MultipleChoiceQuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MultipleChoiceQuestionService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final MultipleChoiceQuestionRepository repository;

    public MultipleChoiceQuestionService(MultipleChoiceQuestionRepository repository) {
        this.repository = repository;
    }

    public PageResult<MultipleChoiceQuestion> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        return new PageResult<>(findAll(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<MultipleChoiceQuestion> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<MultipleChoiceQuestion> findById(Long id) {
        return repository.findById(id);
    }

    public MultipleChoiceQuestion create(MultipleChoiceQuestion input) {
        return repository.save(input);
    }

    public Optional<MultipleChoiceQuestion> update(Long id, MultipleChoiceQuestion input) {
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

    public Optional<MultipleChoiceQuestion> getRandom() {
        List<MultipleChoiceQuestion> items = findAll();
        int size = items.size();
        if (size == 0) {
            return Optional.empty();
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return Optional.of(items.get(index));
    }

    public Optional<MultipleChoiceQuestion> getNext(Long currentId) {
        List<MultipleChoiceQuestion> items = findAll();
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
                .map(MultipleChoiceQuestion::getId)
                .toList();
    }
}




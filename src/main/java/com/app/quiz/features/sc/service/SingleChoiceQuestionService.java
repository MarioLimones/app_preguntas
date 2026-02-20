package com.app.quiz.features.sc.service;

import com.app.quiz.core.PageResult;
import com.app.quiz.features.sc.model.SingleChoiceQuestion;
import com.app.quiz.features.sc.repository.SingleChoiceQuestionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SingleChoiceQuestionService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final SingleChoiceQuestionRepository repository;

    public SingleChoiceQuestionService(SingleChoiceQuestionRepository repository) {
        this.repository = repository;
    }

    public PageResult<SingleChoiceQuestion> findPage(int page, Integer size) {
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        return new PageResult<>(findAll(), page, pageSize);
    }

    public long count() {
        return repository.count();
    }

    public List<SingleChoiceQuestion> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<SingleChoiceQuestion> findById(Long id) {
        return repository.findById(id);
    }

    public SingleChoiceQuestion create(SingleChoiceQuestion input) {
        return repository.save(input);
    }

    public Optional<SingleChoiceQuestion> update(Long id, SingleChoiceQuestion input) {
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

    public Optional<SingleChoiceQuestion> getRandom() {
        List<SingleChoiceQuestion> items = findAll();
        int size = items.size();
        if (size == 0) {
            return Optional.empty();
        }
        int index = ThreadLocalRandom.current().nextInt(size);
        return Optional.of(items.get(index));
    }

    public Optional<SingleChoiceQuestion> getNext(Long currentId) {
        List<SingleChoiceQuestion> items = findAll();
        int size = items.size();
        if (size == 0) {
            return Optional.empty();
        }
        if (currentId == null) {
            return Optional.of(items.get(0));
        }
        for (int i = 0; i < size; i++) {
            if (items.get(i).getId().equals(currentId)) {
                int next = (i + 1) % size;
                return Optional.of(items.get(next));
            }
        }
        return Optional.of(items.get(0));
    }

    public List<Long> getAllIdsSorted() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(SingleChoiceQuestion::getId)
                .toList();
    }
}




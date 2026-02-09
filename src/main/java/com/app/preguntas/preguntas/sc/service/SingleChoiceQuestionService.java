package com.app.preguntas.preguntas.sc.service;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import com.app.preguntas.preguntas.sc.repository.SingleChoiceQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SingleChoiceQuestionService {

    private final SingleChoiceQuestionRepository repository;

    public SingleChoiceQuestionService(SingleChoiceQuestionRepository repository) {
        this.repository = repository;
    }

    public List<SingleChoiceQuestion> findAll() {
        List<SingleChoiceQuestion> items = repository.findAll();
        items.sort(Comparator.comparingLong(SingleChoiceQuestion::getId));
        return items;
    }

    public Optional<SingleChoiceQuestion> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return repository.findById(id);
    }

    public SingleChoiceQuestion create(SingleChoiceQuestion input) {
        SingleChoiceQuestion created = new SingleChoiceQuestion(
            null,
            input.getStatement(),
            input.getOptions(),
            input.getCorrectIndex(),
            input.getExplanation()
        );
        return repository.save(created);
    }

    public Optional<SingleChoiceQuestion> update(Long id, SingleChoiceQuestion input) {
        if (id == null || !repository.existsById(id)) {
            return Optional.empty();
        }
        SingleChoiceQuestion updated = new SingleChoiceQuestion(
            id,
            input.getStatement(),
            input.getOptions(),
            input.getCorrectIndex(),
            input.getExplanation()
        );
        return Optional.of(repository.save(updated));
    }

    public boolean delete(Long id) {
        if (id == null || !repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public Optional<SingleChoiceQuestion> getRandom() {
        List<SingleChoiceQuestion> items = findAll();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        int index = ThreadLocalRandom.current().nextInt(items.size());
        return Optional.of(items.get(index));
    }

    public Optional<SingleChoiceQuestion> getNext(Long currentId) {
        List<SingleChoiceQuestion> items = findAll();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        if (currentId == null) {
            return Optional.of(items.get(0));
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(currentId)) {
                int next = (i + 1) % items.size();
                return Optional.of(items.get(next));
            }
        }
        return Optional.of(items.get(0));
    }

    public List<Long> getAllIdsSorted() {
        List<Long> ids = repository.findAll()
            .stream()
            .map(SingleChoiceQuestion::getId)
            .sorted()
            .toList();
        return ids;
    }
}

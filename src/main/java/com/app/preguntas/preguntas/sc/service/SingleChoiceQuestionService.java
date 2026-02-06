package com.app.preguntas.preguntas.sc.service;

import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SingleChoiceQuestionService {

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, SingleChoiceQuestion> store = new ConcurrentHashMap<>();

    public List<SingleChoiceQuestion> findAll() {
        List<SingleChoiceQuestion> items = new ArrayList<>(store.values());
        items.sort(Comparator.comparingLong(SingleChoiceQuestion::getId));
        return items;
    }

    public Optional<SingleChoiceQuestion> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public SingleChoiceQuestion create(SingleChoiceQuestion input) {
        Long id = idSequence.incrementAndGet();
        SingleChoiceQuestion created = new SingleChoiceQuestion(
            id,
            input.getStatement(),
            input.getOptions(),
            input.getCorrectIndex(),
            input.getExplanation()
        );
        store.put(id, created);
        return created;
    }

    public Optional<SingleChoiceQuestion> update(Long id, SingleChoiceQuestion input) {
        if (id == null || !store.containsKey(id)) {
            return Optional.empty();
        }
        SingleChoiceQuestion updated = new SingleChoiceQuestion(
            id,
            input.getStatement(),
            input.getOptions(),
            input.getCorrectIndex(),
            input.getExplanation()
        );
        store.put(id, updated);
        return Optional.of(updated);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
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
                return Optional.of(items.get(Math.min(i + 1, items.size() - 1)));
            }
        }
        return Optional.of(items.get(0));
    }

    public List<Long> getAllIdsSorted() {
        List<Long> ids = new ArrayList<>(store.keySet());
        ids.sort(Long::compareTo);
        return ids;
    }
}

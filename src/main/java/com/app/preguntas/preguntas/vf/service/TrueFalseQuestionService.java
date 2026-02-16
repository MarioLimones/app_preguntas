package com.app.preguntas.preguntas.vf.service;

import com.app.preguntas.preguntas.vf.model.TrueFalseQuestion;
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
public class TrueFalseQuestionService {

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, TrueFalseQuestion> store = new ConcurrentHashMap<>();

    public List<TrueFalseQuestion> findAll() {
        List<TrueFalseQuestion> items = new ArrayList<>(store.values());
        items.sort(Comparator.comparingLong(TrueFalseQuestion::getId));
        return items;
    }

    public Optional<TrueFalseQuestion> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(id));
    }

    public TrueFalseQuestion create(TrueFalseQuestion input) {
        Long id = idSequence.incrementAndGet();
        TrueFalseQuestion created = new TrueFalseQuestion(
            id,
            input.getStatement(),
            input.getCorrectAnswer(),
            input.getExplanation()
        );
        store.put(id, created);
        return created;
    }

    public Optional<TrueFalseQuestion> update(Long id, TrueFalseQuestion input) {
        if (id == null || !store.containsKey(id)) {
            return Optional.empty();
        }
        TrueFalseQuestion updated = new TrueFalseQuestion(
            id,
            input.getStatement(),
            input.getCorrectAnswer(),
            input.getExplanation()
        );
        store.put(id, updated);
        return Optional.of(updated);
    }

    public boolean delete(Long id) {
        return store.remove(id) != null;
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
        List<Long> ids = new ArrayList<>(store.keySet());
        ids.sort(Long::compareTo);
        return ids;
    }
}

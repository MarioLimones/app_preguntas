package com.app.preguntas.preguntas.common.service;

import com.app.preguntas.preguntas.common.model.QuizResult;
import com.app.preguntas.preguntas.common.repository.QuizResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizResultService {

    private final QuizResultRepository repository;

    public QuizResultService(QuizResultRepository repository) {
        this.repository = repository;
    }

    public QuizResult save(QuizResult result) {
        return repository.save(result);
    }

    public List<QuizResult> findByUsername(String username) {
        return repository.findByUsernameOrderByCompletedAtDesc(username);
    }

    public List<QuizResult> findAll() {
        return repository.findAll();
    }

    public Optional<QuizResult> findById(String id) {
        return repository.findById(id);
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}

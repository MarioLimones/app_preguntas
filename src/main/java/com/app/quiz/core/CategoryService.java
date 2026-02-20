package com.app.quiz.core;

import com.app.quiz.core.Category;
import com.app.quiz.core.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Optional<Category> findById(String id) {
        return repository.findById(id);
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public Optional<Category> update(String id, Category category) {
        if (repository.existsById(id)) {
            category.setId(id);
            return Optional.of(repository.save(category));
        }
        return Optional.empty();
    }

    public boolean delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}




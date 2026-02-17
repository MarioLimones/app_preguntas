package com.app.preguntas.preguntas.mc.repository;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
}

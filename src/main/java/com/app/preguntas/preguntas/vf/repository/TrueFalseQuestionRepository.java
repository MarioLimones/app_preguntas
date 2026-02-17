package com.app.preguntas.preguntas.vf.repository;

import com.app.preguntas.preguntas.vf.model.TrueFalseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrueFalseQuestionRepository extends JpaRepository<TrueFalseQuestion, Long> {
}

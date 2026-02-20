package com.app.quiz.features.vf.repository;

import com.app.quiz.features.vf.model.TrueFalseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrueFalseQuestionRepository extends JpaRepository<TrueFalseQuestion, Long> {
}




package com.app.quiz.features.sc.repository;

import com.app.quiz.features.sc.model.SingleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleChoiceQuestionRepository extends JpaRepository<SingleChoiceQuestion, Long> {
}




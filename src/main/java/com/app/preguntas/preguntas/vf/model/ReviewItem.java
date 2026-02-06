package com.app.preguntas.preguntas.vf.model;

public class ReviewItem {

    private final TrueFalseQuestion question;
    private final Boolean userAnswer;

    public ReviewItem(TrueFalseQuestion question, Boolean userAnswer) {
        this.question = question;
        this.userAnswer = userAnswer;
    }

    public TrueFalseQuestion getQuestion() {
        return question;
    }

    public Boolean getUserAnswer() {
        return userAnswer;
    }

    public boolean isCorrect() {
        if (question == null || userAnswer == null) {
            return false;
        }
        return userAnswer.equals(question.getCorrectAnswer());
    }
}

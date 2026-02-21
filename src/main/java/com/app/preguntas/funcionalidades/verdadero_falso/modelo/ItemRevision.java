package com.app.preguntas.funcionalidades.verdadero_falso.model;

public class ItemRevision {

    private final PreguntaVerdaderoFalso question;
    private final Boolean userAnswer;

    public ItemRevision(PreguntaVerdaderoFalso question, Boolean userAnswer) {
        this.question = question;
        this.userAnswer = userAnswer;
    }

    public PreguntaVerdaderoFalso getQuestion() {
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





package com.app.preguntas.funcionalidades.seleccion_unica.model;

public class SeleccionUnicaItemRevision {

    private final PreguntaSeleccionUnica question;
    private final Integer userAnswer;

    public SeleccionUnicaItemRevision(PreguntaSeleccionUnica question, Integer userAnswer) {
        this.question = question;
        this.userAnswer = userAnswer;
    }

    public PreguntaSeleccionUnica getQuestion() {
        return question;
    }

    public Integer getUserAnswer() {
        return userAnswer;
    }

    public boolean isCorrect() {
        if (question == null || userAnswer == null || question.getCorrectIndex() == null) {
            return false;
        }
        return userAnswer.equals(question.getCorrectIndex());
    }
}





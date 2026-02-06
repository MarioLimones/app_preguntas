package com.app.preguntas.preguntas.sc.model;

import java.util.ArrayList;
import java.util.List;

public class SingleChoiceQuestion {

    private Long id;
    private String statement;
    private List<String> options = new ArrayList<>();
    private Integer correctIndex;
    private String explanation;

    public SingleChoiceQuestion() {
    }

    public SingleChoiceQuestion(Long id, String statement, List<String> options, Integer correctIndex, String explanation) {
        this.id = id;
        this.statement = statement;
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
    }

    public Integer getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(Integer correctIndex) {
        this.correctIndex = correctIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

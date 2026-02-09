package com.app.preguntas.preguntas.mc.model;

import com.app.preguntas.preguntas.common.model.BaseQuestion;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceQuestion extends BaseQuestion {

    private List<String> options = new ArrayList<>();
    private List<Integer> correctIndexes = new ArrayList<>();

    public MultipleChoiceQuestion() {
        super();
    }

    public MultipleChoiceQuestion(Long id,
                                  String statement,
                                  List<String> options,
                                  List<Integer> correctIndexes,
                                  String explanation) {
        super(id, statement, explanation);
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        this.correctIndexes = correctIndexes != null ? new ArrayList<>(correctIndexes) : new ArrayList<>();
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
    }

    public List<Integer> getCorrectIndexes() {
        return correctIndexes;
    }

    public void setCorrectIndexes(List<Integer> correctIndexes) {
        this.correctIndexes = correctIndexes != null ? new ArrayList<>(correctIndexes) : new ArrayList<>();
    }
}

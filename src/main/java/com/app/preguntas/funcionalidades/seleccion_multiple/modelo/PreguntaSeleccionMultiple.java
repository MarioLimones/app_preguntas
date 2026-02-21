package com.app.preguntas.funcionalidades.seleccion_multiple.model;

import com.app.preguntas.nucleo.PreguntaBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mc_questions")
public class PreguntaSeleccionMultiple extends PreguntaBase {

    @ElementCollection
    @CollectionTable(name = "mc_question_options")
    @OrderColumn(name = "option_index")
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "mc_question_correct_indexes")
    @Column(name = "correct_index")
    private List<Integer> correctIndexes = new ArrayList<>();

    public PreguntaSeleccionMultiple() {
        super();
    }

    public PreguntaSeleccionMultiple(Long id,
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





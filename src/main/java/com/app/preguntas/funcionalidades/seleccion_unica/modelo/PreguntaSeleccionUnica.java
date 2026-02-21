package com.app.preguntas.funcionalidades.seleccion_unica.model;

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
@Table(name = "sc_questions")
public class PreguntaSeleccionUnica extends PreguntaBase {

    @ElementCollection
    @CollectionTable(name = "sc_question_options")
    @OrderColumn(name = "option_index")
    @Column(name = "option_text")
    private List<String> options = new ArrayList<>();
    private Integer correctIndex;

    public PreguntaSeleccionUnica() {
        super();
    }

    public PreguntaSeleccionUnica(Long id, String statement, List<String> options, Integer correctIndex, String explanation) {
        super(id, statement, explanation);
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        this.correctIndex = correctIndex;
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
}





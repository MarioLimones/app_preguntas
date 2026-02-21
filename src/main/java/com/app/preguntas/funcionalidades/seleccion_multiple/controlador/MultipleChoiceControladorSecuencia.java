package com.app.preguntas.funcionalidades.seleccion_multiple.controller;

import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.SeleccionMultipleFormularioRespuesta;
import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.PreguntaSeleccionMultiple;
import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.SeleccionMultipleItemRevision;
import com.app.preguntas.funcionalidades.seleccion_multiple.modelo.SeleccionMultipleSesionSecuencia;
import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.SeleccionMultipleServicioPreguntas;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/mc/secuencia")
public class SeleccionMultipleControladorSecuencia {

    private static final String SESSION_KEY = "mcSesionSecuencia";

    private final SeleccionMultipleServicioPreguntas service;

    public SeleccionMultipleControladorSecuencia(SeleccionMultipleServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/mc/preguntas";
        }
        session.setAttribute(SESSION_KEY, new SeleccionMultipleSesionSecuencia(ids));
        return "redirect:/mc/secuencia";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SeleccionMultipleSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/secuencia/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<PreguntaSeleccionMultiple> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/secuencia/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new SeleccionMultipleFormularioRespuesta());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", buildDisplay(sequence.getAnswers().get(questionId)));
        return "seleccion_multiple/secuencia";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("FormularioRespuesta") SeleccionMultipleFormularioRespuesta FormularioRespuesta,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        SeleccionMultipleSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/secuencia/start";
        }
        Optional<PreguntaSeleccionMultiple> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/secuencia";
        }
        List<Integer> selected = normalizeIndexes(FormularioRespuesta.getSelectedIndexes());
        if (!isValidAnswer(selected, question.get())) {
            bindingResult.rejectValue("selectedIndexes", "invalid", "Selecciona opciones validas.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("sequence", sequence);
            return "seleccion_multiple/secuencia";
        }
        sequence.getAnswers().put(questionId, selected);
        boolean correct = isCorrect(selected, question.get().getCorrectIndexes());
        model.addAttribute("question", question.get());
        model.addAttribute("sequence", sequence);
        model.addAttribute("result", correct);
        model.addAttribute("answered", buildDisplay(selected));
        return "seleccion_multiple/secuencia";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        SeleccionMultipleSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/secuencia/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/mc/secuencia";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        SeleccionMultipleSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/secuencia/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/mc/secuencia";
    }

    @GetMapping("/revision")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SeleccionMultipleSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/mc/secuencia/start";
        }
        List<SeleccionMultipleItemRevision> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, List<Integer>> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            PreguntaSeleccionMultiple question = service.findById(id).orElse(null);
            SeleccionMultipleItemRevision item = new SeleccionMultipleItemRevision(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "seleccion_multiple/revision";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/mc/secuencia/start";
    }

    private SeleccionMultipleSesionSecuencia getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof SeleccionMultipleSesionSecuencia) {
            return (SeleccionMultipleSesionSecuencia) stored;
        }
        return null;
    }

    private boolean isValidAnswer(List<Integer> selectedIndexes, PreguntaSeleccionMultiple question) {
        if (selectedIndexes == null || selectedIndexes.isEmpty() || question.getOptions() == null) {
            return false;
        }
        for (Integer index : selectedIndexes) {
            if (index == null || index < 0 || index >= question.getOptions().size()) {
                return false;
            }
        }
        return true;
    }

    private boolean isCorrect(List<Integer> selectedIndexes, List<Integer> correctIndexes) {
        Set<Integer> selected = new HashSet<>(selectedIndexes != null ? selectedIndexes : List.of());
        Set<Integer> correct = new HashSet<>(correctIndexes != null ? correctIndexes : List.of());
        if (correct.isEmpty()) {
            return false;
        }
        return selected.equals(correct);
    }

    private List<Integer> normalizeIndexes(List<Integer> selectedIndexes) {
        if (selectedIndexes == null) {
            return new ArrayList<>();
        }
        Set<Integer> unique = new HashSet<>(selectedIndexes);
        List<Integer> normalized = new ArrayList<>(unique);
        normalized.sort(Integer::compareTo);
        return normalized;
    }

    private String buildDisplay(List<Integer> indexes) {
        if (indexes == null || indexes.isEmpty()) {
            return "-";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(indexes.get(i) + 1);
        }
        return builder.toString();
    }
}






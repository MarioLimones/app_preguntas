package com.app.preguntas.funcionalidades.seleccion_unica.controller;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaFormularioRespuesta;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaItemRevision;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaSesionSecuencia;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/sc/secuencia")
public class SeleccionUnicaControladorSecuencia {

    private static final String SESSION_KEY = "scSesionSecuencia";

    private final SeleccionUnicaServicioPreguntas service;

    public SeleccionUnicaControladorSecuencia(SeleccionUnicaServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/preguntas";
        }
        session.setAttribute(SESSION_KEY, new SeleccionUnicaSesionSecuencia(ids));
        return "redirect:/sc/secuencia";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SeleccionUnicaSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/secuencia/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<PreguntaSeleccionUnica> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/secuencia/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new SeleccionUnicaFormularioRespuesta());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", sequence.getAnswers().get(questionId));
        return "seleccion_unica/secuencia";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("FormularioRespuesta") SeleccionUnicaFormularioRespuesta FormularioRespuesta,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        SeleccionUnicaSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/secuencia/start";
        }
        Optional<PreguntaSeleccionUnica> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/secuencia";
        }
        if (!isValidAnswer(FormularioRespuesta.getSelectedIndex(), question.get())) {
            bindingResult.rejectValue("selectedIndex", "invalid", "Selecciona una opcion valida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("sequence", sequence);
            return "seleccion_unica/secuencia";
        }
        sequence.getAnswers().put(questionId, FormularioRespuesta.getSelectedIndex());
        boolean correct = FormularioRespuesta.getSelectedIndex().equals(question.get().getCorrectIndex());
        model.addAttribute("question", question.get());
        model.addAttribute("sequence", sequence);
        model.addAttribute("result", correct);
        model.addAttribute("answered", FormularioRespuesta.getSelectedIndex());
        return "seleccion_unica/secuencia";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        SeleccionUnicaSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/secuencia/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/sc/secuencia";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        SeleccionUnicaSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/secuencia/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/sc/secuencia";
    }

    @GetMapping("/revision")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SeleccionUnicaSesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/sc/secuencia/start";
        }
        List<SeleccionUnicaItemRevision> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, Integer> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            PreguntaSeleccionUnica question = service.findById(id).orElse(null);
            SeleccionUnicaItemRevision item = new SeleccionUnicaItemRevision(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "seleccion_unica/revision";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/sc/secuencia/start";
    }

    private SeleccionUnicaSesionSecuencia getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof SeleccionUnicaSesionSecuencia) {
            return (SeleccionUnicaSesionSecuencia) stored;
        }
        return null;
    }

    private boolean isValidAnswer(Integer selectedIndex, PreguntaSeleccionUnica question) {
        if (selectedIndex == null || question.getOptions() == null) {
            return false;
        }
        return selectedIndex >= 0 && selectedIndex < question.getOptions().size();
    }
}






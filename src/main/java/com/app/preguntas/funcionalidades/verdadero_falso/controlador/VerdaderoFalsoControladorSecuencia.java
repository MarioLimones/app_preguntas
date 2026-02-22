package com.app.preguntas.funcionalidades.verdadero_falso.controlador;

import com.app.preguntas.funcionalidades.verdadero_falso.modelo.FormularioRespuesta;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.ItemRevision;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.SesionSecuencia;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
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
@RequestMapping("/vf/secuencia")
public class VerdaderoFalsoControladorSecuencia {

    private static final String SESSION_KEY = "vfSesionSecuencia";

    private final VerdaderoFalsoServicioPreguntas service;

    public VerdaderoFalsoControladorSecuencia(VerdaderoFalsoServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/preguntas";
        }
        session.setAttribute(SESSION_KEY, new SesionSecuencia(ids));
        return "redirect:/vf/secuencia";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/secuencia/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<PreguntaVerdaderoFalso> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/secuencia/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new FormularioRespuesta());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", sequence.getAnswers().get(questionId));
        return "verdadero_falso/secuencia";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
            @RequestParam Boolean userAnswer,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        SesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/secuencia/start";
        }
        Optional<PreguntaVerdaderoFalso> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/secuencia";
        }

        // Guardar la respuesta del usuario
        sequence.getAnswers().put(questionId, userAnswer);

        // Calcular si es correcta
        boolean correct = userAnswer.equals(question.get().getCorrectAnswer());

        // Preparar el modelo para mostrar el resultado
        model.addAttribute("question", question.get());
        model.addAttribute("sequence", sequence);
        model.addAttribute("result", correct);
        model.addAttribute("answered", userAnswer);

        return "verdadero_falso/secuencia";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        SesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/secuencia/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/vf/secuencia";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        SesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/secuencia/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/vf/secuencia";
    }

    @GetMapping("/revision")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SesionSecuencia sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/vf/secuencia/start";
        }
        List<ItemRevision> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, Boolean> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            PreguntaVerdaderoFalso question = service.findById(id).orElse(null);
            ItemRevision item = new ItemRevision(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "verdadero_falso/revision";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/vf/secuencia/start";
    }

    private SesionSecuencia getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof SesionSecuencia) {
            return (SesionSecuencia) stored;
        }
        return null;
    }
}










package com.app.quiz.features.vf.controller;

import com.app.quiz.features.vf.model.AnswerForm;
import com.app.quiz.features.vf.model.ReviewItem;
import com.app.quiz.features.vf.model.SequenceSession;
import com.app.quiz.features.vf.model.TrueFalseQuestion;
import com.app.quiz.features.vf.service.TrueFalseQuestionService;
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
@RequestMapping("/vf/sequence")
public class TrueFalseSequenceController {

    private static final String SESSION_KEY = "vfSequenceSession";

    private final TrueFalseQuestionService service;

    public TrueFalseSequenceController(TrueFalseQuestionService service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/questions";
        }
        session.setAttribute(SESSION_KEY, new SequenceSession(ids));
        return "redirect:/vf/sequence";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/sequence/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<TrueFalseQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/sequence/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new AnswerForm());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", sequence.getAnswers().get(questionId));
        return "vf/sequence";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
            @RequestParam Boolean userAnswer,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        SequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/sequence/start";
        }
        Optional<TrueFalseQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/sequence";
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

        return "vf/sequence";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        SequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/sequence/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/vf/sequence";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        SequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/vf/sequence/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/vf/sequence";
    }

    @GetMapping("/review")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/vf/sequence/start";
        }
        List<ReviewItem> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, Boolean> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            TrueFalseQuestion question = service.findById(id).orElse(null);
            ReviewItem item = new ReviewItem(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "vf/review";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/vf/sequence/start";
    }

    private SequenceSession getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof SequenceSession) {
            return (SequenceSession) stored;
        }
        return null;
    }
}




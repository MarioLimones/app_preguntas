package com.app.preguntas.preguntas.sc.controller;

import com.app.preguntas.preguntas.sc.model.SingleChoiceAnswerForm;
import com.app.preguntas.preguntas.sc.model.SingleChoiceQuestion;
import com.app.preguntas.preguntas.sc.model.SingleChoiceReviewItem;
import com.app.preguntas.preguntas.sc.model.SingleChoiceSequenceSession;
import com.app.preguntas.preguntas.sc.service.SingleChoiceQuestionService;
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
@RequestMapping("/sc/sequence")
public class SingleChoiceSequenceController {

    private static final String SESSION_KEY = "scSequenceSession";

    private final SingleChoiceQuestionService service;

    public SingleChoiceSequenceController(SingleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/questions";
        }
        session.setAttribute(SESSION_KEY, new SingleChoiceSequenceSession(ids));
        return "redirect:/sc/sequence";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SingleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/sequence/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<SingleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/sequence/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new SingleChoiceAnswerForm());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", sequence.getAnswers().get(questionId));
        return "sc/sequence";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("answerForm") SingleChoiceAnswerForm answerForm,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        SingleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/sequence/start";
        }
        Optional<SingleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/sequence";
        }
        if (!isValidAnswer(answerForm.getSelectedIndex(), question.get())) {
            bindingResult.rejectValue("selectedIndex", "invalid", "Selecciona una opcion valida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("sequence", sequence);
            return "sc/sequence";
        }
        sequence.getAnswers().put(questionId, answerForm.getSelectedIndex());
        boolean correct = answerForm.getSelectedIndex().equals(question.get().getCorrectIndex());
        model.addAttribute("question", question.get());
        model.addAttribute("sequence", sequence);
        model.addAttribute("result", correct);
        model.addAttribute("answered", answerForm.getSelectedIndex());
        return "sc/sequence";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        SingleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/sequence/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/sc/sequence";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        SingleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/sc/sequence/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/sc/sequence";
    }

    @GetMapping("/review")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        SingleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/sc/sequence/start";
        }
        List<SingleChoiceReviewItem> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, Integer> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            SingleChoiceQuestion question = service.findById(id).orElse(null);
            SingleChoiceReviewItem item = new SingleChoiceReviewItem(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "sc/review";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/sc/sequence/start";
    }

    private SingleChoiceSequenceSession getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof SingleChoiceSequenceSession) {
            return (SingleChoiceSequenceSession) stored;
        }
        return null;
    }

    private boolean isValidAnswer(Integer selectedIndex, SingleChoiceQuestion question) {
        if (selectedIndex == null || question.getOptions() == null) {
            return false;
        }
        return selectedIndex >= 0 && selectedIndex < question.getOptions().size();
    }
}

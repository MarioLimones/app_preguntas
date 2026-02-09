package com.app.preguntas.preguntas.mc.controller;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceAnswerForm;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceReviewItem;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceSequenceSession;
import com.app.preguntas.preguntas.mc.service.MultipleChoiceQuestionService;
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
@RequestMapping("/mc/sequence")
public class MultipleChoiceSequenceController {

    private static final String SESSION_KEY = "mcSequenceSession";

    private final MultipleChoiceQuestionService service;

    public MultipleChoiceSequenceController(MultipleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping("/start")
    public String start(HttpSession session, RedirectAttributes redirectAttributes) {
        List<Long> ids = service.getAllIdsSorted();
        if (ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/mc/questions";
        }
        session.setAttribute(SESSION_KEY, new MultipleChoiceSequenceSession(ids));
        return "redirect:/mc/sequence";
    }

    @GetMapping
    public String current(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        MultipleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/sequence/start";
        }
        Long questionId = sequence.getCurrentQuestionId();
        Optional<MultipleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/sequence/start";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new MultipleChoiceAnswerForm());
        model.addAttribute("sequence", sequence);
        model.addAttribute("answered", buildDisplay(sequence.getAnswers().get(questionId)));
        return "mc/sequence";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("answerForm") MultipleChoiceAnswerForm answerForm,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        MultipleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/sequence/start";
        }
        Optional<MultipleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/sequence";
        }
        List<Integer> selected = normalizeIndexes(answerForm.getSelectedIndexes());
        if (!isValidAnswer(selected, question.get())) {
            bindingResult.rejectValue("selectedIndexes", "invalid", "Selecciona opciones validas.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("sequence", sequence);
            return "mc/sequence";
        }
        sequence.getAnswers().put(questionId, selected);
        boolean correct = isCorrect(selected, question.get().getCorrectIndexes());
        model.addAttribute("question", question.get());
        model.addAttribute("sequence", sequence);
        model.addAttribute("result", correct);
        model.addAttribute("answered", buildDisplay(selected));
        return "mc/sequence";
    }

    @PostMapping("/next")
    public String next(HttpSession session, RedirectAttributes redirectAttributes) {
        MultipleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/sequence/start";
        }
        if (sequence.hasNext()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() + 1);
        }
        return "redirect:/mc/sequence";
    }

    @PostMapping("/prev")
    public String prev(HttpSession session, RedirectAttributes redirectAttributes) {
        MultipleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "Inicia una secuencia para continuar.");
            return "redirect:/mc/sequence/start";
        }
        if (sequence.hasPrevious()) {
            sequence.setCurrentIndex(sequence.getCurrentIndex() - 1);
        }
        return "redirect:/mc/sequence";
    }

    @GetMapping("/review")
    public String review(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        MultipleChoiceSequenceSession sequence = getSequence(session);
        if (sequence == null) {
            redirectAttributes.addFlashAttribute("error", "No hay secuencia activa.");
            return "redirect:/mc/sequence/start";
        }
        List<MultipleChoiceReviewItem> items = new ArrayList<>();
        int correctCount = 0;
        Map<Long, List<Integer>> answers = sequence.getAnswers();
        for (Long id : sequence.getQuestionIds()) {
            MultipleChoiceQuestion question = service.findById(id).orElse(null);
            MultipleChoiceReviewItem item = new MultipleChoiceReviewItem(question, answers.get(id));
            items.add(item);
            if (item.isCorrect()) {
                correctCount++;
            }
        }
        model.addAttribute("items", items);
        model.addAttribute("total", sequence.getQuestionIds().size());
        model.addAttribute("correctCount", correctCount);
        model.addAttribute("incorrectCount", sequence.getQuestionIds().size() - correctCount);
        return "mc/review";
    }

    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/mc/sequence/start";
    }

    private MultipleChoiceSequenceSession getSequence(HttpSession session) {
        Object stored = session.getAttribute(SESSION_KEY);
        if (stored instanceof MultipleChoiceSequenceSession) {
            return (MultipleChoiceSequenceSession) stored;
        }
        return null;
    }

    private boolean isValidAnswer(List<Integer> selectedIndexes, MultipleChoiceQuestion question) {
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

package com.app.preguntas.preguntas.mc.controller;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceAnswerForm;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/mc/play")
public class MultipleChoicePlayController {

    private final MultipleChoiceQuestionService service;

    public MultipleChoicePlayController(MultipleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping("/random")
    public String random(Model model, RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.getRandom();
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/mc/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new MultipleChoiceAnswerForm());
        model.addAttribute("mode", "random");
        return "mc/play";
    }

    @GetMapping("/next")
    public String next(@RequestParam(required = false) Long currentId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.getNext(currentId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/mc/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new MultipleChoiceAnswerForm());
        model.addAttribute("mode", "next");
        return "mc/play";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("answerForm") MultipleChoiceAnswerForm answerForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/questions";
        }
        List<Integer> selected = normalizeIndexes(answerForm.getSelectedIndexes());
        if (!isValidAnswer(selected, question.get())) {
            bindingResult.rejectValue("selectedIndexes", "invalid", "Selecciona opciones validas.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("mode", "random");
            return "mc/play";
        }
        boolean correct = isCorrect(selected, question.get().getCorrectIndexes());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValues", buildDisplay(selected));
        model.addAttribute("mode", "random");
        return "mc/play";
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

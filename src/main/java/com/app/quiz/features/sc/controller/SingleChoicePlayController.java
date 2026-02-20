package com.app.quiz.features.sc.controller;

import com.app.quiz.features.sc.model.SingleChoiceAnswerForm;
import com.app.quiz.features.sc.model.SingleChoiceQuestion;
import com.app.quiz.features.sc.service.SingleChoiceQuestionService;
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

import java.util.Optional;

@Controller
@RequestMapping("/sc/play")
public class SingleChoicePlayController {

    private final SingleChoiceQuestionService service;

    public SingleChoicePlayController(SingleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping("/random")
    public String random(Model model, RedirectAttributes redirectAttributes) {
        Optional<SingleChoiceQuestion> question = service.getRandom();
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new SingleChoiceAnswerForm());
        model.addAttribute("mode", "random");
        return "sc/play";
    }

    @GetMapping("/next")
    public String next(@RequestParam(required = false) Long currentId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Optional<SingleChoiceQuestion> question = service.getNext(currentId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new SingleChoiceAnswerForm());
        model.addAttribute("mode", "next");
        return "sc/play";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("answerForm") SingleChoiceAnswerForm answerForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Optional<SingleChoiceQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/questions";
        }
        if (!isValidAnswer(answerForm.getSelectedIndex(), question.get())) {
            bindingResult.rejectValue("selectedIndex", "invalid", "Selecciona una opcion valida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("mode", "random");
            return "sc/play";
        }
        boolean correct = answerForm.getSelectedIndex().equals(question.get().getCorrectIndex());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", answerForm.getSelectedIndex());
        model.addAttribute("mode", "random");
        return "sc/play";
    }

    private boolean isValidAnswer(Integer selectedIndex, SingleChoiceQuestion question) {
        if (selectedIndex == null || question.getOptions() == null) {
            return false;
        }
        return selectedIndex >= 0 && selectedIndex < question.getOptions().size();
    }
}




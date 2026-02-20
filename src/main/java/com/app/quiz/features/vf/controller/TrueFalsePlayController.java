package com.app.quiz.features.vf.controller;

import com.app.quiz.features.vf.model.AnswerForm;
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

import java.util.Optional;

@Controller
@RequestMapping("/vf/play")
public class TrueFalsePlayController {

    private final TrueFalseQuestionService service;

    public TrueFalsePlayController(TrueFalseQuestionService service) {
        this.service = service;
    }

    @GetMapping("/random")
    public String random(Model model, RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.getRandom();
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new AnswerForm());
        model.addAttribute("mode", "random");
        return "vf/play";
    }

    @GetMapping("/next")
    public String next(@RequestParam(required = false) Long currentId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.getNext(currentId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new AnswerForm());
        model.addAttribute("mode", "next");
        return "vf/play";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("answerForm") AnswerForm answerForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("mode", "random");
            return "vf/play";
        }
        boolean correct = answerForm.getUserAnswer().equals(question.get().getCorrectAnswer());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", answerForm.getUserAnswer());
        model.addAttribute("mode", "random");
        return "vf/play";
    }
}




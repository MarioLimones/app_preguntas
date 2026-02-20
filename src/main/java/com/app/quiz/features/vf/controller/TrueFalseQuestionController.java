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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/vf/questions")
public class TrueFalseQuestionController {

    private final TrueFalseQuestionService service;

    public TrueFalseQuestionController(TrueFalseQuestionService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            Model model) {
        model.addAttribute("pageResult", service.findPage(page, size));
        return "vf/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("question", new TrueFalseQuestion());
        model.addAttribute("mode", "create");
        return "vf/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("question") TrueFalseQuestion question,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "vf/form";
        }
        TrueFalseQuestion created = service.create(question);
        redirectAttributes.addFlashAttribute("success", "Pregunta creada.");
        return "redirect:/vf/questions/" + created.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new AnswerForm());
        return "vf/detail";
    }

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable Long id,
            @Valid @ModelAttribute("answerForm") AnswerForm answerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            return "vf/detail";
        }
        boolean correct = answerForm.getUserAnswer().equals(question.get().getCorrectAnswer());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", answerForm.getUserAnswer());
        return "vf/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<TrueFalseQuestion> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("mode", "edit");
        return "vf/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("question") TrueFalseQuestion question,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "vf/form";
        }
        Optional<TrueFalseQuestion> updated = service.update(id, question);
        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta actualizada.");
        return "redirect:/vf/questions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!service.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/questions";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta eliminada.");
        return "redirect:/vf/questions";
    }
}




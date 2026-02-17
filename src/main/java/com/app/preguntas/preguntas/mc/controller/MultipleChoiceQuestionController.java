package com.app.preguntas.preguntas.mc.controller;

import com.app.preguntas.preguntas.mc.model.MultipleChoiceAnswerForm;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestion;
import com.app.preguntas.preguntas.mc.model.MultipleChoiceQuestionForm;
import com.app.preguntas.preguntas.mc.service.MultipleChoiceQuestionService;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/mc/questions")
public class MultipleChoiceQuestionController {

    private final MultipleChoiceQuestionService service;

    public MultipleChoiceQuestionController(MultipleChoiceQuestionService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            Model model) {
        model.addAttribute("pageResult", service.findPage(page, size));
        return "mc/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new MultipleChoiceQuestionForm());
        model.addAttribute("mode", "create");
        return "mc/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") MultipleChoiceQuestionForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> options = validateAndParseOptions(form, bindingResult);
        List<Integer> correctIndexes = validateCorrectIndexes(form.getCorrectIndexesText(), options, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "mc/form";
        }
        MultipleChoiceQuestion created = service.create(toQuestion(form, options, correctIndexes));
        redirectAttributes.addFlashAttribute("success", "Pregunta creada.");
        return "redirect:/mc/questions/" + created.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/questions";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new MultipleChoiceAnswerForm());
        return "mc/detail";
    }

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable Long id,
            @Valid @ModelAttribute("answerForm") MultipleChoiceAnswerForm answerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.findById(id);
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
            return "mc/detail";
        }
        boolean correct = isCorrect(selected, question.get().getCorrectIndexes());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValues", buildDisplay(selected));
        return "mc/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MultipleChoiceQuestion> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/questions";
        }
        MultipleChoiceQuestionForm form = fromQuestion(question.get());
        model.addAttribute("form", form);
        model.addAttribute("mode", "edit");
        model.addAttribute("id", id);
        return "mc/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("form") MultipleChoiceQuestionForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> options = validateAndParseOptions(form, bindingResult);
        List<Integer> correctIndexes = validateCorrectIndexes(form.getCorrectIndexesText(), options, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("id", id);
            return "mc/form";
        }
        Optional<MultipleChoiceQuestion> updated = service.update(id, toQuestion(form, options, correctIndexes));
        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/questions";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta actualizada.");
        return "redirect:/mc/questions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!service.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/mc/questions";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta eliminada.");
        return "redirect:/mc/questions";
    }

    private List<String> validateAndParseOptions(MultipleChoiceQuestionForm form, BindingResult bindingResult) {
        List<String> options = parseOptions(form.getOptionsText());
        if (options.size() < 2) {
            bindingResult.rejectValue("optionsText", "min", "Debes ingresar al menos dos opciones.");
        }
        if (hasBlankLines(form.getOptionsText())) {
            bindingResult.rejectValue("optionsText", "blank", "No se permiten opciones vacias.");
        }
        if (hasDuplicateOptions(options)) {
            bindingResult.rejectValue("optionsText", "dup", "No se permiten opciones duplicadas.");
        }
        return options;
    }

    private List<Integer> validateCorrectIndexes(String correctIndexesText,
            List<String> options,
            BindingResult bindingResult) {
        List<Integer> parsed = parseIndexes(correctIndexesText, bindingResult);
        if (parsed.isEmpty()) {
            bindingResult.rejectValue("correctIndexesText", "min", "Debes indicar al menos una opcion correcta.");
            return parsed;
        }
        if (!options.isEmpty()) {
            for (Integer index : parsed) {
                if (index == null || index < 1 || index > options.size()) {
                    bindingResult.rejectValue("correctIndexesText", "range",
                            "Las opciones correctas deben estar entre 1 y " + options.size() + ".");
                    return new ArrayList<>();
                }
            }
        }
        if (hasDuplicateIndexes(parsed)) {
            bindingResult.rejectValue("correctIndexesText", "dup", "No se permiten opciones duplicadas.");
            return new ArrayList<>();
        }
        List<Integer> zeroBased = new ArrayList<>();
        for (Integer value : parsed) {
            zeroBased.add(value - 1);
        }
        zeroBased.sort(Integer::compareTo);
        return zeroBased;
    }

    private MultipleChoiceQuestion toQuestion(MultipleChoiceQuestionForm form,
            List<String> options,
            List<Integer> correctIndexes) {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setStatement(form.getStatement());
        question.setOptions(options);
        question.setCorrectIndexes(correctIndexes);
        question.setExplanation(form.getExplanation());
        return question;
    }

    private MultipleChoiceQuestionForm fromQuestion(MultipleChoiceQuestion question) {
        MultipleChoiceQuestionForm form = new MultipleChoiceQuestionForm();
        form.setStatement(question.getStatement());
        form.setOptionsText(String.join("\n", question.getOptions()));
        form.setCorrectIndexesText(buildDisplay(question.getCorrectIndexes()));
        form.setExplanation(question.getExplanation());
        return form;
    }

    private List<String> parseOptions(String optionsText) {
        List<String> options = new ArrayList<>();
        if (optionsText == null) {
            return options;
        }
        String[] lines = optionsText.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                options.add(trimmed);
            }
        }
        return options;
    }

    private List<Integer> parseIndexes(String indexesText, BindingResult bindingResult) {
        List<Integer> indexes = new ArrayList<>();
        if (indexesText == null) {
            return indexes;
        }
        String[] parts = indexesText.trim().split("[,\\s]+");
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            try {
                indexes.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException ex) {
                bindingResult.rejectValue("correctIndexesText", "invalid", "Ingresa numeros separados por coma.");
                return new ArrayList<>();
            }
        }
        return indexes;
    }

    private boolean hasBlankLines(String optionsText) {
        if (optionsText == null) {
            return false;
        }
        String[] lines = optionsText.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateOptions(List<String> options) {
        Set<String> seen = new HashSet<>();
        for (String option : options) {
            String normalized = option.trim().toLowerCase();
            if (!seen.add(normalized)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateIndexes(List<Integer> values) {
        Set<Integer> seen = new HashSet<>();
        for (Integer value : values) {
            if (!seen.add(value)) {
                return true;
            }
        }
        return false;
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

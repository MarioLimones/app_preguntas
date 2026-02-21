package com.app.preguntas.funcionalidades.seleccion_unica.controller;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaFormularioRespuesta;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaFormularioPregunta;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaQuestionImportResult;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaQuestionImportService;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/sc/preguntas")
public class SeleccionUnicaControladorPreguntas {

    private final SeleccionUnicaServicioPreguntas service;
    private final SeleccionUnicaQuestionImportService importService;

    public SeleccionUnicaControladorPreguntas(SeleccionUnicaServicioPreguntas service,
            SeleccionUnicaQuestionImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            Model model) {
        model.addAttribute("ResultadoPagina", service.findPage(page, size));
        return "seleccion_unica/listado";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new SeleccionUnicaFormularioPregunta());
        model.addAttribute("mode", "create");
        return "seleccion_unica/formulario";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") SeleccionUnicaFormularioPregunta form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> options = validateAndParseOptions(form, bindingResult);
        Integer correctIndex = validateCorrectIndex(form.getCorrectIndex(), options, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "seleccion_unica/formulario";
        }
        PreguntaSeleccionUnica created = service.create(toQuestion(form, options, correctIndex));
        redirectAttributes.addFlashAttribute("success", "Pregunta creada.");
        return "redirect:/sc/preguntas/" + created.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new SeleccionUnicaFormularioRespuesta());
        return "seleccion_unica/detalle";
    }

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable Long id,
            @Valid @ModelAttribute("FormularioRespuesta") SeleccionUnicaFormularioRespuesta FormularioRespuesta,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        if (!isValidAnswer(FormularioRespuesta.getSelectedIndex(), question.get())) {
            bindingResult.rejectValue("selectedIndex", "invalid", "Selecciona una opcion valida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            return "seleccion_unica/detalle";
        }
        boolean correct = FormularioRespuesta.getSelectedIndex().equals(question.get().getCorrectIndex());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", FormularioRespuesta.getSelectedIndex());
        return "seleccion_unica/detalle";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        SeleccionUnicaFormularioPregunta form = fromQuestion(question.get());
        model.addAttribute("form", form);
        model.addAttribute("mode", "edit");
        model.addAttribute("id", id);
        return "seleccion_unica/formulario";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("form") SeleccionUnicaFormularioPregunta form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        List<String> options = validateAndParseOptions(form, bindingResult);
        Integer correctIndex = validateCorrectIndex(form.getCorrectIndex(), options, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            model.addAttribute("id", id);
            return "seleccion_unica/formulario";
        }
        Optional<PreguntaSeleccionUnica> updated = service.update(id, toQuestion(form, options, correctIndex));
        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta actualizada.");
        return "redirect:/sc/preguntas/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!service.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta eliminada.");
        return "redirect:/sc/preguntas";
    }

    @PostMapping("/subir")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        SeleccionUnicaQuestionImportResult result = importService.importFile(file);
        if (result.getCreatedCount() > 0) {
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Se cargaron " + result.getCreatedCount() + " de " + result.getTotal() + " preguntas.");
        }
        if (!result.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Algunas preguntas no pudieron cargarse.");
            redirectAttributes.addFlashAttribute("uploadErrors", result.getErrors());
        }
        if (result.getCreatedCount() == 0 && result.getErrors().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudieron cargar preguntas.");
        }
        return "redirect:/sc/preguntas";
    }

    private List<String> validateAndParseOptions(SeleccionUnicaFormularioPregunta form, BindingResult bindingResult) {
        List<String> options = parseOptions(form.getOptionsText());
        if (options.size() < 2) {
            bindingResult.rejectValue("optionsText", "min", "Debes ingresar al menos dos opciones.");
        }
        if (hasBlankLines(form.getOptionsText())) {
            bindingResult.rejectValue("optionsText", "blank", "No se permiten opciones vacias.");
        }
        if (hasDuplicates(options)) {
            bindingResult.rejectValue("optionsText", "dup", "No se permiten opciones duplicadas.");
        }
        return options;
    }

    private Integer validateCorrectIndex(Integer correctIndex, List<String> options, BindingResult bindingResult) {
        if (correctIndex == null) {
            return null;
        }
        if (options.isEmpty()) {
            bindingResult.rejectValue("correctIndex", "invalid", "La opcion correcta es invalida.");
            return null;
        }
        if (correctIndex < 1 || correctIndex > options.size()) {
            bindingResult.rejectValue("correctIndex", "range",
                    "La opcion correcta debe estar entre 1 y " + options.size() + ".");
            return null;
        }
        return correctIndex - 1;
    }

    private PreguntaSeleccionUnica toQuestion(SeleccionUnicaFormularioPregunta form, List<String> options, Integer correctIndex) {
        PreguntaSeleccionUnica question = new PreguntaSeleccionUnica();
        question.setStatement(form.getStatement());
        question.setOptions(options);
        question.setCorrectIndex(correctIndex);
        question.setExplanation(form.getExplanation());
        return question;
    }

    private SeleccionUnicaFormularioPregunta fromQuestion(PreguntaSeleccionUnica question) {
        SeleccionUnicaFormularioPregunta form = new SeleccionUnicaFormularioPregunta();
        form.setStatement(question.getStatement());
        form.setOptionsText(String.join("\n", question.getOptions()));
        if (question.getCorrectIndex() != null) {
            form.setCorrectIndex(question.getCorrectIndex() + 1);
        }
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

    private boolean hasDuplicates(List<String> options) {
        Set<String> seen = new HashSet<>();
        for (String option : options) {
            String normalized = option.trim().toLowerCase();
            if (!seen.add(normalized)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidAnswer(Integer selectedIndex, PreguntaSeleccionUnica question) {
        if (selectedIndex == null || question.getOptions() == null) {
            return false;
        }
        return selectedIndex >= 0 && selectedIndex < question.getOptions().size();
    }
}






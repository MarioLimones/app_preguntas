package com.app.preguntas.funcionalidades.seleccion_unica.controlador;

import com.app.preguntas.funcionalidades.seleccion_unica.modelo.SeleccionUnicaFormularioRespuesta;
import com.app.preguntas.funcionalidades.seleccion_unica.modelo.PreguntaSeleccionUnica;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
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
@RequestMapping("/sc/practicar")
public class SeleccionUnicaControladorPractica {

    private final SeleccionUnicaServicioPreguntas service;

    public SeleccionUnicaControladorPractica(SeleccionUnicaServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping("/random")
    public String random(Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.getRandom();
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new SeleccionUnicaFormularioRespuesta());
        model.addAttribute("mode", "random");
        return "seleccion_unica/practicar";
    }

    @GetMapping("/next")
    public String next(@RequestParam(required = false) Long currentId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.getNext(currentId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/sc/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new SeleccionUnicaFormularioRespuesta());
        model.addAttribute("mode", "next");
        return "seleccion_unica/practicar";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
            @Valid @ModelAttribute("answerForm") SeleccionUnicaFormularioRespuesta answerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<PreguntaSeleccionUnica> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/sc/preguntas";
        }
        if (!isValidAnswer(answerForm.getSelectedIndex(), question.get())) {
            bindingResult.rejectValue("selectedIndex", "invalid", "Selecciona una opcion valida.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("mode", "random");
            return "seleccion_unica/practicar";
        }
        boolean correct = answerForm.getSelectedIndex().equals(question.get().getCorrectIndex());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", answerForm.getSelectedIndex());
        model.addAttribute("mode", "random");
        return "seleccion_unica/practicar";
    }

    private boolean isValidAnswer(Integer selectedIndex, PreguntaSeleccionUnica question) {
        if (selectedIndex == null || question.getOptions() == null) {
            return false;
        }
        return selectedIndex >= 0 && selectedIndex < question.getOptions().size();
    }
}

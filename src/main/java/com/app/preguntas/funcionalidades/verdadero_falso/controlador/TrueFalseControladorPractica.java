package com.app.preguntas.funcionalidades.verdadero_falso.controller;

import com.app.preguntas.funcionalidades.verdadero_falso.modelo.FormularioRespuesta;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
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
@RequestMapping("/vf/practicar")
public class VerdaderoFalsoControladorPractica {

    private final VerdaderoFalsoServicioPreguntas service;

    public VerdaderoFalsoControladorPractica(VerdaderoFalsoServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping("/random")
    public String random(Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.getRandom();
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new FormularioRespuesta());
        model.addAttribute("mode", "random");
        return "verdadero_falso/practicar";
    }

    @GetMapping("/next")
    public String next(@RequestParam(required = false) Long currentId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.getNext(currentId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay preguntas disponibles.");
            return "redirect:/vf/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("FormularioRespuesta", new FormularioRespuesta());
        model.addAttribute("mode", "next");
        return "verdadero_falso/practicar";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long questionId,
                         @Valid @ModelAttribute("FormularioRespuesta") FormularioRespuesta FormularioRespuesta,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.findById(questionId);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            model.addAttribute("mode", "random");
            return "verdadero_falso/practicar";
        }
        boolean correct = FormularioRespuesta.getUserAnswer().equals(question.get().getCorrectAnswer());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", FormularioRespuesta.getUserAnswer());
        model.addAttribute("mode", "random");
        return "verdadero_falso/practicar";
    }
}






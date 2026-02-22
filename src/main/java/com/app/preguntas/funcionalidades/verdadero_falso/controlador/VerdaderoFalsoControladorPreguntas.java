package com.app.preguntas.funcionalidades.verdadero_falso.controlador;

import com.app.preguntas.funcionalidades.verdadero_falso.modelo.FormularioRespuesta;
import com.app.preguntas.funcionalidades.verdadero_falso.modelo.PreguntaVerdaderoFalso;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
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
@RequestMapping("/vf/preguntas")
public class VerdaderoFalsoControladorPreguntas {

    private final VerdaderoFalsoServicioPreguntas service;

    public VerdaderoFalsoControladorPreguntas(VerdaderoFalsoServicioPreguntas service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size,
            Model model) {
        model.addAttribute("pageResult", service.findPage(page, size));
        return "verdadero_falso/listado";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("question", new PreguntaVerdaderoFalso());
        model.addAttribute("mode", "create");
        return "verdadero_falso/formulario";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("question") PreguntaVerdaderoFalso question,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "create");
            return "verdadero_falso/formulario";
        }
        PreguntaVerdaderoFalso created = service.create(question);
        redirectAttributes.addFlashAttribute("success", "Pregunta creada.");
        return "redirect:/vf/preguntas/" + created.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("answerForm", new FormularioRespuesta());
        return "verdadero_falso/detalle";
    }

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable Long id,
            @Valid @ModelAttribute("answerForm") FormularioRespuesta answerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question.get());
            return "verdadero_falso/detalle";
        }
        boolean correct = answerForm.getUserAnswer().equals(question.get().getCorrectAnswer());
        model.addAttribute("question", question.get());
        model.addAttribute("result", correct);
        model.addAttribute("answerValue", answerForm.getUserAnswer());
        return "verdadero_falso/detalle";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PreguntaVerdaderoFalso> question = service.findById(id);
        if (question.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        model.addAttribute("question", question.get());
        model.addAttribute("mode", "edit");
        return "verdadero_falso/formulario";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
            @Valid @ModelAttribute("question") PreguntaVerdaderoFalso question,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mode", "edit");
            return "verdadero_falso/formulario";
        }
        Optional<PreguntaVerdaderoFalso> updated = service.update(id, question);
        if (updated.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta actualizada.");
        return "redirect:/vf/preguntas/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!service.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "La pregunta no existe.");
            return "redirect:/vf/preguntas";
        }
        redirectAttributes.addFlashAttribute("success", "Pregunta eliminada.");
        return "redirect:/vf/preguntas";
    }
}

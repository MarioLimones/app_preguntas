package com.app.preguntas;

import com.app.preguntas.nucleo.Categoria;
import com.app.preguntas.nucleo.ServicioCategoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class ControladorCategorias {

    private final ServicioCategoria servicio;

    public ControladorCategorias(ServicioCategoria servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", servicio.findAll());
        return "categorias/list";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        if (name == null || name.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio.");
            return "redirect:/categories";
        }
        Categoria categoria = new Categoria(name.trim(), description != null ? description.trim() : null);
        servicio.create(categoria);
        redirectAttributes.addFlashAttribute("success", "Categoria creada.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        if (!servicio.delete(id)) {
            redirectAttributes.addFlashAttribute("error", "La categoria no existe.");
            return "redirect:/categories";
        }
        redirectAttributes.addFlashAttribute("success", "Categoria eliminada.");
        return "redirect:/categories";
    }
}

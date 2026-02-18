package com.app.preguntas.preguntas.common.controller;

import com.app.preguntas.preguntas.common.model.Category;
import com.app.preguntas.preguntas.common.service.CategoryService;
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
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", service.findAll());
        return "categories/list";
    }

    @PostMapping
    public String create(@RequestParam String name,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        Category category = new Category(name, description);
        service.create(category);
        redirectAttributes.addFlashAttribute("success", "Categoría creada exitosamente");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        if (service.delete(id)) {
            redirectAttributes.addFlashAttribute("success", "Categoría eliminada");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se encontró la categoría");
        }
        return "redirect:/categories";
    }
}

package com.app.preguntas.preguntas.upload.controller;

import com.app.preguntas.preguntas.upload.service.QuestionImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/upload")
public class QuestionUploadController {

    private final QuestionImportService importService;

    public QuestionUploadController(QuestionImportService importService) {
        this.importService = importService;
    }

    @GetMapping
    public String uploadForm() {
        return "upload/form";
    }

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor selecciona un archivo");
            return "redirect:/upload";
        }

        try {
            QuestionImportService.ImportResult result = importService.importQuestions(file);

            if (result.getSuccessCount() > 0) {
                redirectAttributes.addFlashAttribute("success",
                        String.format("Importación completada: %d preguntas importadas correctamente",
                                result.getSuccessCount()));
            }

            if (result.getErrorCount() > 0) {
                redirectAttributes.addFlashAttribute("warning",
                        String.format("%d preguntas con errores", result.getErrorCount()));
                redirectAttributes.addFlashAttribute("errors", result.getErrors());
            }

            return "redirect:/upload";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al procesar el archivo: " + e.getMessage());
            return "redirect:/upload";
        }
    }
}

package com.app.preguntas.funcionalidades.upload;

import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.SeleccionMultipleServicioPreguntas;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subir")
public class QuestionUploadController {

    private final QuestionUploadService uploadService;

    public QuestionUploadController(QuestionUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @GetMapping
    public String showForm() {
        return "subir/formulario";
    }

    @PostMapping
    public String handleUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        try {
            QuestionUploadResult result = uploadService.importQuestions(file);
            redirectAttributes.addFlashAttribute("success", "Proceso completado.");
            if (result.getCreatedCount() > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Se han importado " + result.getCreatedCount() + " preguntas con éxito.");
            }
            if (!result.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("errors", result.getErrors());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el archivo: " + e.getMessage());
        }
        return "redirect:/upload";
    }
}


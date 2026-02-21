package com.app.preguntas;

import com.app.preguntas.funcionalidades.seleccion_multiple.servicio.SeleccionMultipleServicioPreguntas;
import com.app.preguntas.funcionalidades.seleccion_unica.servicio.SeleccionUnicaServicioPreguntas;
import com.app.preguntas.funcionalidades.verdadero_falso.servicio.VerdaderoFalsoServicioPreguntas;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ControladorInicio {

    private final VerdaderoFalsoServicioPreguntas vfService;
    private final SeleccionUnicaServicioPreguntas scService;
    private final SeleccionMultipleServicioPreguntas mcService;

    public ControladorInicio(VerdaderoFalsoServicioPreguntas vfService,
            SeleccionUnicaServicioPreguntas scService,
            SeleccionMultipleServicioPreguntas mcService) {
        this.vfService = vfService;
        this.scService = scService;
        this.mcService = mcService;
    }

    @GetMapping({ "/", "/home" })
    public String home(Model model) {
        model.addAttribute("vfCount", vfService.count());
        model.addAttribute("scCount", scService.count());
        model.addAttribute("mcCount", mcService.count());
        return "inicio";
    }

    @GetMapping("/debug/error500")
    public String error500() {
        throw new RuntimeException("Error 500 de prueba");
    }

    @GetMapping("/debug/error404")
    public String error404() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error 404 de prueba");
    }
}





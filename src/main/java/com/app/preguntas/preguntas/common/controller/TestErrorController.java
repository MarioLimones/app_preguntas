package com.app.preguntas.preguntas.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/debug")
public class TestErrorController {

    @GetMapping("/error500")
    public String triggerError() {
        throw new RuntimeException("¡PUM! Esto es un error de prueba para ver la página 500.");
    }
}

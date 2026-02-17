package com.app.preguntas.preguntas.common.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", message != null ? message.toString() : "Error inesperado");
        model.addAttribute("requestUri", uri != null ? uri.toString() : "");

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        }
        return "error/500";
    }
}

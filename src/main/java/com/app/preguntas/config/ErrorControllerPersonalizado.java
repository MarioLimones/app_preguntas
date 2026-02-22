package com.app.preguntas.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class ErrorControllerPersonalizado {

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public String handleErrorHtml(HttpServletRequest request, Model model) {
        HttpStatus status = resolveStatus(request);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        model.addAttribute("requestUri", requestUri);
        model.addAttribute("status", status.value());
        model.addAttribute("error", status.getReasonPhrase());
        return resolveView(status);
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleErrorJson(HttpServletRequest request) {
        HttpStatus status = resolveStatus(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        body.put("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        return ResponseEntity.status(status).body(body);
    }

    private HttpStatus resolveStatus(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode instanceof Integer code) {
            return HttpStatus.valueOf(code);
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveView(HttpStatus status) {
        if (status == HttpStatus.NOT_FOUND) {
            return "error/404";
        }
        if (status == HttpStatus.BAD_REQUEST) {
            return "error/400";
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return "error/503";
        }
        return "error/500";
    }
}

package com.app.preguntas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Object handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), ex, request);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            HttpMessageNotReadableException.class,
            ConstraintViolationException.class
    })
    public Object handleBadRequest(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Solicitud invalida.", ex, request);
    }

    @ExceptionHandler(DataAccessException.class)
    public Object handleDataAccess(DataAccessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de datos no disponible.", ex, request);
    }

    @ExceptionHandler(RestClientException.class)
    public Object handleUpstream(RestClientException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_GATEWAY, "Error comunicando con servicio externo.", ex, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "Recurso no encontrado.", ex, request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleAny(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", ex, request);
    }

    private Object buildResponse(HttpStatus status, String message, Exception ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message != null ? message : ex.getMessage());
            body.put("path", request.getRequestURI());
            return ResponseEntity.status(status).body(body);
        }
        ModelAndView mav = new ModelAndView(resolveView(status));
        mav.setStatus(status);
        mav.addObject("errorMessage", message != null ? message : ex.getMessage());
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path != null && path.startsWith("/api/")) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
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

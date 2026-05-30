package com.pragma.Inventario.shared.web.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pragma.Inventario.producto.application.exception.ProductNotFoundException;
import com.pragma.Inventario.security.application.exception.UserAlreadyExistsException;
import com.pragma.Inventario.security.application.exception.UserNotFoundException;
import com.pragma.Inventario.shared.web.error.ErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponse payload = new ErrorResponse("not_found", ex.getMessage(), null);
        return new ResponseEntity<>(payload, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse payload = new ErrorResponse("not_found", ex.getMessage(), null);
        return new ResponseEntity<>(payload, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(UserAlreadyExistsException ex) {
        ErrorResponse payload = new ErrorResponse("conflict", ex.getMessage(), null);
        return new ResponseEntity<>(payload, new HttpHeaders(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        ErrorResponse payload = new ErrorResponse("validation_error", "Validation failed", Map.of("fields", errors));
        return new ResponseEntity<>(payload, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse payload = new ErrorResponse("internal_error", "Ha ocurrido un error interno", null);
        return new ResponseEntity<>(payload, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
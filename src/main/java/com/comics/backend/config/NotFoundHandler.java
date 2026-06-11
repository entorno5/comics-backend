package com.comics.backend.config;

import com.comics.backend.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles all uncaught exceptions and returns standardized error responses.
 */
@RestControllerAdvice
@Slf4j
public class NotFoundHandler {

    /**
     * Handles ResourceNotFoundException (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getErrorMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Handles DuplicateResourceException (409 Conflict)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource detected: {}", ex.getErrorMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), HttpStatus.CONFLICT.value()));
    }

    /**
     * Handles ValidationException (400 Bad Request)
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {}", ex.getErrorMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    /**
     * Handles AuthenticationException (401 Unauthorized)
     */
    @ExceptionHandler(com.comics.backend.exceptions.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(com.comics.backend.exceptions.AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getErrorMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    /**
     * Handles Spring Security AuthenticationException (401 Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpringAuthenticationException(AuthenticationException ex) {
        log.warn("Spring authentication error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("ERR_AUTHENTICATION_FAILED", "Authentication failed", HttpStatus.UNAUTHORIZED.value()));
    }

    /**
     * Handles bean validation errors (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Method argument validation failed");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", "ERR_VALIDATION_FAILED");
        response.put("message", "Validation failed");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles 404 Not Found for non-existent routes
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoHandlerFoundException ex) {
        log.warn("Route not found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ERR_ROUTE_NOT_FOUND", "The requested route does not exist", HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Handles all other uncaught exceptions (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERR_INTERNAL_SERVER_ERROR", 
                       "An unexpected error occurred. Please try again later.",
                       HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    /**
     * Standard error response structure
     */
    record ErrorResponse(String code, String message, int status, LocalDateTime timestamp) {
        public ErrorResponse(String code, String message, int status) {
            this(code, message, status, LocalDateTime.now());
        }
    }
}

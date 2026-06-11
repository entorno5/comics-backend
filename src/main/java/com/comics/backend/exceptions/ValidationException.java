package com.comics.backend.exceptions;

/**
 * Exception thrown for validation errors in request data.
 * HTTP Status: 400 BAD_REQUEST
 */
public class ValidationException extends BaseException {

    public ValidationException(String fieldName, String message) {
        super("ERR_VALIDATION_FAILED", 
              String.format("Validation failed for field '%s': %s", fieldName, message));
    }

    public ValidationException(String message) {
        super("ERR_VALIDATION_FAILED", message);
    }
}

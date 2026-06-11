package com.comics.backend.exceptions;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * HTTP Status: 409 CONFLICT
 */
public class DuplicateResourceException extends BaseException {

    public DuplicateResourceException(String resourceName, String fieldName, String value) {
        super("ERR_DUPLICATE_RESOURCE", 
              String.format("%s with %s '%s' already exists", resourceName, fieldName, value));
    }

    public DuplicateResourceException(String message) {
        super("ERR_DUPLICATE_RESOURCE", message);
    }
}

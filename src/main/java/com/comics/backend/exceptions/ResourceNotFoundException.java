package com.comics.backend.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 * HTTP Status: 404 NOT_FOUND
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resourceName, String identifier) {
        super("ERR_RESOURCE_NOT_FOUND", 
              String.format("%s with identifier '%s' not found", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super("ERR_RESOURCE_NOT_FOUND", message);
    }
}

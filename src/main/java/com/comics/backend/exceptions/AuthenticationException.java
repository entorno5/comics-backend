package com.comics.backend.exceptions;

/**
 * Exception thrown for authentication and authorization failures.
 * HTTP Status: 401 UNAUTHORIZED or 403 FORBIDDEN
 */
public class AuthenticationException extends BaseException {

    public AuthenticationException(String message) {
        super("ERR_AUTHENTICATION_FAILED", message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super("ERR_AUTHENTICATION_FAILED", message, cause);
    }
}

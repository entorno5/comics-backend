package com.comics.backend.exceptions;

/**
 * Base exception class for all custom application exceptions.
 * All application-specific exceptions should extend this class.
 */
public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    public BaseException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

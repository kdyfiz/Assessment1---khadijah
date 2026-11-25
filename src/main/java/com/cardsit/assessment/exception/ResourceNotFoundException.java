package com.cardsit.assessment.exception;

/**
 * Thrown when a requested resource does not exist (local card or remote user).
 *
 * <p>Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

package com.cardsit.assessment.exception;

/**
 * Thrown for expected business-rule failures (e.g. duplicate card number,
 * upstream 3rd-party call failure).
 *
 * <p>Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}

package com.possessionmanager.service;

/**
 * Indicates that user-supplied domain data does not satisfy a validation rule.
 */
public final class ValidationException extends RuntimeException {

    /**
     * Creates a validation failure with a user-facing explanation.
     *
     * @param message explanation of the failed validation rule.
     */
    public ValidationException(String message) {
        super(message);
    }
}

package com.possessionmanager.storage;

/**
 * Indicates that application data could not be loaded or saved safely.
 */
public final class StorageException extends RuntimeException {

    /**
     * Creates a storage failure with its original cause.
     *
     * @param message user-facing explanation of the storage failure.
     * @param cause underlying file or JSON failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

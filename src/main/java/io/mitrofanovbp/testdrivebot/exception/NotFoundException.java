package io.mitrofanovbp.testdrivebot.exception;

/**
 * Thrown when an entity is not found.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

package io.mitrofanovbp.testdrivebot.exception;

/**
 * Thrown for business conflicts (e.g., double booking).
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

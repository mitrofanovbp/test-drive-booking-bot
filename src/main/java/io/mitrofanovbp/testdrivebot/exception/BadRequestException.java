package io.mitrofanovbp.testdrivebot.exception;

/**
 * Thrown for invalid inputs.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

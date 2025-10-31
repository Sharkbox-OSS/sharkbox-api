package dev.sharkbox.api.exception;

/**
 * Exception thrown when a user attempts to vote on their own content.
 */
public class SelfVoteNotAllowedException extends RuntimeException {
    
    public SelfVoteNotAllowedException(String message) {
        super(message);
    }
    
    public SelfVoteNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}

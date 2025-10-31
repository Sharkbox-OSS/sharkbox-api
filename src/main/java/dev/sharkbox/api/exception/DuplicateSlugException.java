package dev.sharkbox.api.exception;

public class DuplicateSlugException extends RuntimeException {
    
    public DuplicateSlugException(String slug) {
        super("Box with slug '" + slug + "' already exists");
    }
}

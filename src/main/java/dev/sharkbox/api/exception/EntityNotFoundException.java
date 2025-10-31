package dev.sharkbox.api.exception;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " not found with id: " + id);
    }
}

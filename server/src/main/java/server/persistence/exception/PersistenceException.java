package server.persistence.exception;

/**
 * Exception thrown when error occurs in persistence layer
 */
public class PersistenceException extends Exception {
    /**
     * @param message Message
     */
    public PersistenceException(String message) {
        super(message);
    }
}

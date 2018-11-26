package client.service.exception;

/**
 * Exception thrown at problem in Service layer
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
}

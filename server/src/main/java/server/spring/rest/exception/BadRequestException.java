package server.spring.rest.exception;

/**
 * Exception thrown when an invalid request reaches the controllers
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message){
        super(message);
    }
}

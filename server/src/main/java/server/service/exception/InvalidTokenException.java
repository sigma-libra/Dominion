package server.service.exception;

/**
 * Exception thrown when a client presents an invalid token as authorization
 */
public class InvalidTokenException extends Exception {
    public InvalidTokenException(){
        super();
    }

    public InvalidTokenException(String message){
        super(message);
    }
}

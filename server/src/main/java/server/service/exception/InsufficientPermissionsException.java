package server.service.exception;

/**
 * Exception thrown at attempt to do something in server for which client has no authorisation
 */
public class InsufficientPermissionsException extends Exception {
    public InsufficientPermissionsException(String message){
        super(message);
    }
}

package server.service.exception;

/**
 * Exception thrown to pass on attempt to load a non-existent user
 */
public class UserNotFoundException extends ServiceException {
    public UserNotFoundException(String message){
        super(message);
    }
}

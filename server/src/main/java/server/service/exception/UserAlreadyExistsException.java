package server.service.exception;

/**
 * Exception thrown to pass on attempt to create already existing user
 */
public class UserAlreadyExistsException extends ServiceException {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}

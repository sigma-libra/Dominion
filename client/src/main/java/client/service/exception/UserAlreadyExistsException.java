package client.service.exception;

/**
 * Exception thrown at attempt to add a user with a name that is already taken
 */
public class UserAlreadyExistsException extends UserNotAddedException {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}

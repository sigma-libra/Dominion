package client.service.exception;

/**
 * Exception thrown if a problem occurred while adding a new user
 */
public class UserNotAddedException extends ServiceException {
    public UserNotAddedException(String username) {
        super(String.format("The user name \"%s\" is already in use", username));
    }
}

package client.service.exception;

/**
 * Exception thrown if a problem occurred while a user was being modified
 */
public class UserNotModifiedException extends ServiceException {
    public UserNotModifiedException(String message) {
        super(message);
    }
}

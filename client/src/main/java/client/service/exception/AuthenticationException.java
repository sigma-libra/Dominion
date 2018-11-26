package client.service.exception;

/**
 * Exception thrown at authentification error
 */
public class AuthenticationException extends ServiceException {
    public AuthenticationException(String message) {
        super(message);
    }
}

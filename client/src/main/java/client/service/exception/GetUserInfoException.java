package client.service.exception;

/**
 * Exception thrown when there was a problem getting user info
 */
public class GetUserInfoException extends ServiceException {
    public GetUserInfoException(String username, String message) {
        super(String.format("Error getting user \"%s\"'s information: %s", username, message));
    }
}

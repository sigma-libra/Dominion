package client.service.exception;

/**
 * Exception thrown to pass on server exception
 */
public class ServerException extends ServiceException {
    public ServerException(String message) {
        super(message);
    }
}

package shared.domain.exceptions;

/**
 * Thrown when the server receives an invalid response for an interactive card effect from the client
 */
public class InvalidArgumentsException extends GameException {
    public InvalidArgumentsException(String message){
        super(message);
    }
}

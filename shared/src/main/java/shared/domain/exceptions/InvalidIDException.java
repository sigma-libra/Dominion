package shared.domain.exceptions;

/**
 * Thrown when an invalid card pile id or hand card id is received
 */
public class InvalidIDException extends GameException {
    public InvalidIDException(String message){
        super(message);
    }
}

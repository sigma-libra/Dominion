package shared.domain.exceptions;

/**
 * Base class for exceptions thrown when something goes wrong in the game simulation
 */
public class GameException extends Exception {
    public GameException(String message){
        super(message);
    }
}

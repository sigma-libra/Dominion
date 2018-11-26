package server.persistence.exception;

/**
 * Exception thrown at attempt to load a player not in database
 */
public class UserNotFoundException extends PersistenceException {
    public UserNotFoundException(String message){
        super(message);
    }
}

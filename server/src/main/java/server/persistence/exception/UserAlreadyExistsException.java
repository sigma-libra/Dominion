package server.persistence.exception;

/**
 * Exception thrown at attempt to create a user that already exists
 */
public class UserAlreadyExistsException extends PersistenceException {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}


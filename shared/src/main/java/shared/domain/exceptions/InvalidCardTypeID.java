package shared.domain.exceptions;

/**
 * exception to be thrown if cardtype is invalid
 */
public class InvalidCardTypeID extends Exception {
    public InvalidCardTypeID(int id){
        super(String.format("id = %d", id));
    }
}

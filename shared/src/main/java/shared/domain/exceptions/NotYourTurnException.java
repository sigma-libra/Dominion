package shared.domain.exceptions;

public class NotYourTurnException extends GameException {
    public NotYourTurnException(){
        super("You can only perform this action during your own turn");
    }
}

package shared.domain.effect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;
import shared.domain.exceptions.InvalidArgumentsException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for interactive effects that make the player choose some <T>s out of a list of <T>s
 * @param <T>
 */
public abstract class ChooseSomeEffect<T> extends PlayerChoosesEffect {
    private int from;
    private int upTo;
    private List<T> choices;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //empty constructor required for Jackson deserialization
    public ChooseSomeEffect() {
        this(0, -1);
    }

    public ChooseSomeEffect(int from, int upTo){
        this(new ArrayList<>(), from, upTo);
    }

    public ChooseSomeEffect(List<T> choices, int from, int upTo){
        LOG.info("ChooseSomeEffect");
        this.from = from;
        this.upTo = upTo;
        this.choices = choices;
    }

    /**
     * Validates the received arguments and calls execute(GameState gameState, List<T> arguments)
     * @param gameState
     * @param arguments
     * @throws InvalidArgumentsException if the arguments were invalid
     * @throws GameException
     */
    @Override
    public void execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("execute - ChooseSomeEffect");

        if (getChoices().isEmpty()){
            execute_no_choices(gameState, player);
            return;
        }

        if (arguments == null) {
            throw new InvalidArgumentsException("Received arguments were null");
        }
        if (from > 0 && arguments.length < from && getChoices().size() >= from) {
            throw new InvalidArgumentsException(String.format("Expected at least %d player selections, but got %d", from, arguments.length));
        }
        if (upTo != -1 && arguments.length > upTo && getChoices().size() >= upTo) {
            throw new InvalidArgumentsException(String.format("Expected at most %d player selections, but got %d", upTo, arguments.length));
        }
        if (arguments.length > choices.size()) {
            throw new InvalidArgumentsException(String.format("Expected at most %d player selections, but got %d", choices.size(), arguments.length));
        }
        errorchecked_execute(gameState, player, arguments);
    }

    protected void execute_no_choices(GameState gameState, Player player) throws GameException {
        // most cards will simply do nothing when there's nothing to choose
    }

    /**
     * abstract method where subclasses implement their effects
     *
     * @param gameState
     * @param arguments
     * @throws GameException
     */
    protected abstract void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getUpTo() {
        return upTo;
    }

    public void setUpTo(int upTo) {
        this.upTo = upTo;
    }

    public List<T> getChoices(){
        return choices;
    }

    public void setChoices(List<T> choices){
        this.choices = choices;
    }
}

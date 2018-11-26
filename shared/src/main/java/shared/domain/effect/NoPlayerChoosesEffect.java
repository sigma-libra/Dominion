package shared.domain.effect;

import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

/**
 * @author Alex
 */
public abstract class NoPlayerChoosesEffect extends CardEffect {

    public abstract void execute(GameState gameState, Player player) throws GameException;
}

package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;

import java.lang.invoke.MethodHandles;

/**
 * @author Alex
 */
public class AddCreditEffect extends NoPlayerChoosesEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    int howMuch;

    public AddCreditEffect(int howMuch) {
        LOG.info("AddCreditEffect");
        this.howMuch = howMuch;
    }

    @Override
    public void execute(GameState gameState, Player player) {
        LOG.info("execute - AddCreditEffect");
        gameState.getTurnTracker().addCredit(howMuch);
    }
}

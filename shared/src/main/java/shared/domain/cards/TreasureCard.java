package shared.domain.cards;

import shared.domain.engine.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


/**
 * Class modelling a Treasure Card
 */
public abstract class TreasureCard extends PlayableCard {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * how many coins are generated when the cards is played
     * @return coin value
     */
    public abstract int getCoinValue();

    @Override
    public void applyEffects(GameState gameState) {
        gameState.getTurnTracker().addCredit(getCoinValue());
    }
}

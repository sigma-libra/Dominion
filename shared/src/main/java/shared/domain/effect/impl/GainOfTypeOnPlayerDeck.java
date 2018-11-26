package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.treasures.SilverCard;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;

public class GainOfTypeOnPlayerDeck extends NoPlayerChoosesEffect {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int cardId;

    public GainOfTypeOnPlayerDeck(int cardId) {
        LOG.info("GainOfTypeOnPlayerDeck");
        this.cardId = cardId;
    }

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - GainOfTypeOnPlayerDeck");
        player.getDeck().add(gameState.getSupply().retrieveCardById(cardId));
    }
}

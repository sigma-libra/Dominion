package shared.domain.effect.cardaction.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.cardaction.CardAction;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * CardAction: trashes the chosen cards
 */
public class TrashCardsAction implements CardAction, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void execute(GameState gameState, Player player, List<Card> chosenCards) throws GameException {
        LOG.info("execute - TrashCardsAction");
        // the CardSource has already removed the cards. We have nothing to do.
    }
}

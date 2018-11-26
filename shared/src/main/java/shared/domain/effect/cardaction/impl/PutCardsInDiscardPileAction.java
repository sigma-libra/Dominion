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
 * CardAction: puts the chosen cards into the player's discard pile
 */
public class PutCardsInDiscardPileAction implements CardAction, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public void execute(GameState gameState, Player player, List<Card> chosenCards) throws GameException {
        LOG.info("execute - PutCardsInDiscardPileAction");
        player.getDiscard().addAll(chosenCards);
    }
}

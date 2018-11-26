package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * If the hand contains a copper card, the card is trashed and the player obtains +3 credit
 * Used by Moneylender
 */
public class TrashCopperCardForCoinEffect extends NoPlayerChoosesEffect{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    int howMuch;

    public TrashCopperCardForCoinEffect(int howMuch) {
        LOG.info("TrashCopperCardForCoinEffect");
        this.howMuch = howMuch;
    }

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        LOG.info("execute - TrashCopperCardForCoinEffect");
        List<Card> hand = player.getHand().getCards();
        boolean foundCopperCard = false;
        for(int i = 0; i < hand.size() && !foundCopperCard; i++) {
            if(CopperCard.class.isInstance(hand.get(i))) {
                foundCopperCard = true;
                gameState.getTurnTracker().addCredit(howMuch);
                int[] indice = {i};
                gameState.trashCards(player, indice);
            }
        }
    }
}

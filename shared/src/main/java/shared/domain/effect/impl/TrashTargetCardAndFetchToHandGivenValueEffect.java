package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;

/**
 * Effect allowing a card to be trashed and then a new one brought to hand with a value of up to the price
 * of the trashed card + an extra sum
 */
public class TrashTargetCardAndFetchToHandGivenValueEffect<C extends Card> extends TrashTargetCardsOfGivenTypeEffect{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    int extra;
    Class<C> cardType;

    //empty constructor required for Jackson deserialization
    public TrashTargetCardAndFetchToHandGivenValueEffect(){
        LOG.info("TrashTargetCardAndFetchToHandGivenValueEffect");
    }

    public TrashTargetCardAndFetchToHandGivenValueEffect(int from, int upTo, Class<C> cardType, int extra){
        super(from, upTo, cardType);
        LOG.info("TrashTargetCardAndFetchToHandGivenValueEffect");
        this.extra = extra;
        this.cardType = cardType;
    }


    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - TrashTargetCardAndFetchToHandGivenValueEffect");
        if(arguments.length > 0) {
            Card card = player.getHand().subSetOfType(cardType).get(arguments[0]);
            int newMaxPrice = card.getPrice() + extra;
            gameState.getPendingEffects(player).push(new GainTargetCardOfGivenTypeToHandEffect<>(
                newMaxPrice, cardType));
        }
        gameState.trashCards(player, arguments);
    }
}

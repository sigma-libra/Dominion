package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;

import java.lang.invoke.MethodHandles;

/**
 * Effect allowing trashing only of cards of a given type
 */
public class TrashTargetCardsOfGivenTypeEffect extends TrashTargetCardsEffect{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private Class cardType;

    //empty constructor required for Jackson deserialization
    public TrashTargetCardsOfGivenTypeEffect(){
        LOG.info("TrashTargetCardsOfGivenTypeEffect");
    }

    /**
     * Constructor for effect
     * @param from
     * @param upTo
     * @param cardType
     * @param <C> generic extension of Card
     */
    public <C extends Card> TrashTargetCardsOfGivenTypeEffect(int from, int upTo, Class<C> cardType){
        super(from, upTo);
        LOG.info("TrashTargetCardsOfGivenTypeEffect");
        this.cardType = cardType;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setCardChoices(player.getHand().subSetOfType(cardType));
    }


}

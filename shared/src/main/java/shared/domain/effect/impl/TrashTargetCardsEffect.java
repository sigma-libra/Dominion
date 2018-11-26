package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;


/**
 * @author Alex
 */
public class TrashTargetCardsEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //empty constructor required for Jackson deserialization
    public TrashTargetCardsEffect(){
        LOG.info("TrashTargetCardsEffect");
    }

    public TrashTargetCardsEffect(int from, int upTo){
        super(from, upTo);
        LOG.info("TrashTargetCardsEffect");
    }


    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setCardChoices(player.getHand());
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - TrashTargetCardsEffect");
        gameState.trashCards(player, arguments);
    }
}

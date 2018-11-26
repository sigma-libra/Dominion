package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;

/**
 * @author Alex
 */
public class PutTargetCardFromHandOnTopOfDeckEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //empty constructor required for Jackson deserialization
    public PutTargetCardFromHandOnTopOfDeckEffect(){
        LOG.info("PutTargetCardFromHandOnTopOfDeckEffect");
    }

    public PutTargetCardFromHandOnTopOfDeckEffect(int nbCards) {
        super(nbCards, nbCards);
        LOG.info("PutTargetCardFromHandOnTopOfDeckEffect");
    }


    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setCardChoices(player.getHand());
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - PutTargetCardFromHandOnTopOfDeckEffect");
        gameState.returnCardsToDeck(player, arguments);
    }
}

package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;

/**
 * The Player may choose some cards to discard, and then draws as many cards.
 */
public class DiscardThenDrawEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //empty constructor required for Jackson deserialization
    public DiscardThenDrawEffect(){
        this(0, -1);
    }

    public DiscardThenDrawEffect(int from, int upTo){
        super(from, upTo);
        LOG.info("DiscardThenDrawEffect");
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setCardChoices(player.getHand());
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - DiscardThenDrawEffect");
        gameState.discardCards(player, arguments);
        gameState.drawCards(player, arguments.length);
    }
}

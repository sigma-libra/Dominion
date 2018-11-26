package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.CardEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class SentryReturnToDeckEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Card> topTwo;

    public SentryReturnToDeckEffect(){}

    public SentryReturnToDeckEffect(List<Card> topTwo) {
        this.topTwo = topTwo;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        this.setFrom(1);
        this.setUpTo(1);
        setCardChoices(topTwo);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("error_checked_execute");
        player.getDeck().add(topTwo.get(arguments[0]));
        player.getDeck().add(topTwo.get(Math.abs(arguments[0] - 1)));
    }

}

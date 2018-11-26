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

/**
 * Class implementing effect for sentry:
 * look at top two cards: select any to discard, then return the other to deck
 */
public class SentryDiscardEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Card> topTwo;

    public SentryDiscardEffect(){}


    public SentryDiscardEffect(List<Card> topTwo) {
        this.topTwo = topTwo;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setCardChoices(topTwo);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("error_checked_execute");
        List<Card> dealtWithCards = new ArrayList<>();
        for(int i = 0; i < arguments.length; i++) {
            Card toDiscard = topTwo.get(arguments[i]);
            player.getDiscard().add(toDiscard);
            dealtWithCards.add(toDiscard);
        }

        for(Card c: dealtWithCards){topTwo.remove(c);}

        if(topTwo.size() == 2) {
            gameState.getPendingEffects(player).push(new SentryReturnToDeckEffect(topTwo));
        } else if(topTwo.size() == 1) {
            player.getDeck().add(topTwo.get(0));
        }
    }

}

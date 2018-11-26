package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.kingdoms.Library;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Special sentry effect: look at top top cards of draw: trash and/or discard,
 * and put the rest back in any order: part 1 -> trash and call discard
 */
public class SentryTrashEffect extends ChooseCardsEffect{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private List<Card> topTwo;

    public SentryTrashEffect(){}

    public SentryTrashEffect(int howMany) {
        topTwo = new ArrayList<>();
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        if(player.getDeckSize() < 2) {
            player.refreshDeck();
        }

        topTwo = new ArrayList<>(player.getDeck().getCards().subList(0, Math.min(player.getDeckSize(),2)));
        player.getDeck().removeAll(topTwo);
        setCardChoices(topTwo);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
       LOG.info("errorchecked_execute()");
       List<Card> dealtWithCards = new ArrayList<>();
        for(int i = 0; i < arguments.length; i++) {
            Card toTrash = topTwo.get(arguments[i]);
            gameState.addToTrashPile(toTrash);
            dealtWithCards.add(toTrash);
        }

        for(Card c: dealtWithCards){topTwo.remove(c);}

        if(!topTwo.isEmpty()) {
            gameState.getPendingEffects(player).push(new SentryDiscardEffect(topTwo));
        }
    }


}

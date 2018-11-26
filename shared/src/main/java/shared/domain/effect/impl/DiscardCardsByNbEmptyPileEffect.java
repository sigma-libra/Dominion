package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Effect making hand discard a number of cards equivalent to the number of empty supply piles
 * Used by poacher
 */
public class DiscardCardsByNbEmptyPileEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public DiscardCardsByNbEmptyPileEffect(){
        super(0, 0);
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        List<Integer> choices = player.getHand().stream().map(c -> c.getID()).collect(Collectors.toList());
        setChoices(choices);

        int nbEmptyPiles = 0;
        for(Stack<Card> pile: gameState.getSupply().getPiles()) {
            if(pile.isEmpty()) {
                nbEmptyPiles += 1;
            }
        }
        nbEmptyPiles = player.getHandSize() < nbEmptyPiles ? player.getHandSize(): nbEmptyPiles;

        setFrom(nbEmptyPiles);
        setUpTo(nbEmptyPiles);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("execute - DiscardCardsByNbEmptyPileEffect");
        gameState.discardCards(player, arguments);
    }
}

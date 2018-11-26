package shared.domain.effect.cardsource.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.cardsource.CardSource;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CardSource: the cards in the player's hand
 */
public class HandCardsSource implements CardSource, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public List<Integer> getChoices(GameState gameState, Player player) {
        LOG.info("getChoices");
        return player.getHand().stream().map(c -> c.getID()).collect(Collectors.toList());
    }

    public List<Card> getAndRemoveChosenCards(GameState gameState, Player player, List<Integer> possibleChoices, int[] chosenIndices){
        LOG.info("getAndRemoveChosenCards");
        List<Card> cards = new ArrayList<>();

        Arrays.sort(chosenIndices);
        for (int i=chosenIndices.length-1; i>=0; i--){
            int index = chosenIndices[i];
            Card card = player.getHand().remove(index);
            cards.add(0, card);
        }

        return cards;
    }
}

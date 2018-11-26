package shared.domain.effect.cardsource.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.cardsource.CardSource;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.InvalidIDException;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CardSource: the cards available in the supply piles
 */
public class SupplyPileSource implements CardSource, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    int maxPrice = -1;

    public SupplyPileSource(){
        this(-1);
    }

    public SupplyPileSource(int maxPrice){
        LOG.info("SupplyPileSource");
        this.maxPrice = maxPrice;
    }

    public List<Integer> getChoices(GameState gameState, Player player) {
        LOG.info("getChoices");
        Stream<Card> cards = gameState.getSupply().getPiles().stream().filter(p -> !p.isEmpty()).map(p -> p.peek());

        if (maxPrice >= 0)
            cards = cards.filter(c -> c.getPrice() <= maxPrice);

        return cards.map(c -> c.getID()).collect(Collectors.toList());
    }

    public List<Card> getAndRemoveChosenCards(GameState gameState, Player player, List<Integer> possibleChoices, int[] chosenIndices){
        LOG.info("getAndRemoveChosenCards");
        List<Card> cards = new ArrayList<>();

        Arrays.sort(chosenIndices);
        for (int i=chosenIndices.length-1; i>=0; i--){
            int index = chosenIndices[i];
            int cardID = possibleChoices.get(index);
            Card card;

            try {
                card = gameState.getSupply().retrieveCardById(cardID);
            } catch (InvalidIDException e){
                LOG.error(e.getMessage());
                continue;
            }

            cards.add(0, card);
        }

        return cards;
    }
}

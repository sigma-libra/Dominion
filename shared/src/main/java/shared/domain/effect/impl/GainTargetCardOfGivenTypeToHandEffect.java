package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Effect allowing a player to choose a card of given type from tabletop
 * @param <C>
 */
public class GainTargetCardOfGivenTypeToHandEffect<C extends Card> extends GainTargetCardToHandEffect{

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int maxPrice;
    private Class<C> type;

    public GainTargetCardOfGivenTypeToHandEffect() {
    }

    public GainTargetCardOfGivenTypeToHandEffect(int maxPrice, Class<C> type) {
        super(maxPrice);

        LOG.info("GainTargetCardOfGivenTypeToHandEffect");

        this.maxPrice = maxPrice;
        this.type = type;

    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        List<Integer> choices = new ArrayList<>();

        for (Stack<Card> pile : gameState.getSupply().getPiles()){
            if (!pile.isEmpty()){
                Card card = pile.peek();
                if (type.isInstance(card) && card.getPrice() <= maxPrice) {
                    choices.add(card.getID());
                }
            }
        }

        setChoices(choices);
    }

}

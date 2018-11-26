package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Effect allowing player to gain any card from tabletop with an upper limit on price
 * @author Alex
 */
public class GainTargetCardToHandEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int maxPrice;

    public GainTargetCardToHandEffect() {
        LOG.info("GainTargetCardToHandEffect");
    }

    public GainTargetCardToHandEffect(int maxPrice) {
        super(1, 1);
        LOG.info("GainTargetCardToHandEffect");
        this.maxPrice = maxPrice;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        List<Integer> choices = new ArrayList<>();

        for (Stack<Card> pile : gameState.getSupply().getPiles()){
            if (!pile.isEmpty()){
                Card card = pile.peek();
                if (card.getPrice() <= maxPrice) {
                    choices.add(card.getID());
                }
            }
        }

        setChoices(choices);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute - GainTargetCardToHandEffect");
        //Get the list of cards player could choose from and return id at chosen index
        List<Integer> choices = getChoices();
        Card card = gameState.getSupply().retrieveCardById(choices.get(arguments[0]));
        player.getHand().add(card);
        String message = player.getUser().getUserName() + " gained another " + card.getName();
        gameState.getPlayLog().add(message);
    }
}

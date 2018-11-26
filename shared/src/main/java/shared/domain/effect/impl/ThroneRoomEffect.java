package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class ThroneRoomEffect extends ChooseCardsEffect {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private List<Integer> cardsInHand;


    /**
     * empty constructor required for Jackson deserialization
     */
    public ThroneRoomEffect() {
        LOG.info("ThroneRoomEffect");
    }


    public ThroneRoomEffect(int from, int upTo) {
        super(from,upTo);

        LOG.info("ThroneRoomEffect");
    }

    /**
     * Player can chose from ActionCards in his Hand
     * @param gameState
     * @param player
     */
    @Override
    public void updateChoices(GameState gameState, Player player) {
        cardsInHand = new ArrayList<Integer>();
        int skippy = -1;
        CardPile pileOfActionCardsFromHand = new CardPile();
        for(int i=0;i < player.getHandSize();i++) {
            skippy+= 1;
            if (player.getHand().get(i) instanceof KingdomCard) {
                cardsInHand.add(skippy);
                pileOfActionCardsFromHand.add(player.getHand().get(i));
            }
        }
        LOG.info("No of items in chosable cards in hand: " + cardsInHand.size());
        LOG.info("No of items in Chose Window: " + pileOfActionCardsFromHand.size());

        setCardChoices(pileOfActionCardsFromHand);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        //Only do if there was a card chosen
        if(arguments.length > 0) {
            LOG.info("arguments[] size: " + arguments.length + "    expecting 1");
            gameState.getTurnTracker().addActionsAvailable(1);
            int index = cardsInHand.get(arguments[0]).intValue();


            Card card = player.getHand().get(index);
            LOG.info("Chosen Card: " + card.getName());
            gameState.playCard(player, index);
            player.getCardsPlayedThisTurn().remove(card);
            gameState.getTurnTracker().addActionsAvailable(1);
            player.getHand().add(card);
            for (int i = 0; i < player.getHandSize(); i++) {
                if (player.getHand().get(i).getID() == card.getID()) {
                    gameState.playCard(player, i);
                    break;
                }
            }
        }


    }

}

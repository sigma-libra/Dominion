package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Effect implementing specifically the Library Card's actions (unique):
 * Draw until nbCards cards in hand. Any action cards drawn are showed to player and
 * if not selected, discarded
 */
public class LibraryEffect extends ChooseCardsEffect{

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private int howMany;
    private List<Card> actionCards;

    //necessary deserializer for JSon
    public LibraryEffect(){}


    public LibraryEffect(int howMany) {
        super(-1, -1);
        this.howMany = howMany;
        actionCards = new ArrayList<>();
    }

    @Override
    public void updateChoices(GameState gameState, Player player){

        int nbToDraw = howMany - player.getHandSize();

        if(nbToDraw > player.getDeckSize()) {
            player.refreshDeck();
        }

        //get nb of required cards out of deck and into a list for use
        List<Card> drawnCards = new ArrayList<>(player.getDeck().getCards().subList(0, Math.min(nbToDraw, player.getDeckSize())));
        player.getDeck().removeAll(drawnCards);

        List<Card> notActionCards = new ArrayList<>();
        actionCards.clear();

        //separate into action and non-action cards
        for(int i = 0; i < drawnCards.size(); i++) {
            if(drawnCards.get(i) instanceof KingdomCard) {
                actionCards.add(drawnCards.get(i));

            } else {
                notActionCards.add(drawnCards.get(i));
            }
        }
        //put non-actioncards in hand
        player.getHand().addAll(notActionCards);

        //set action cards to be selected to keep
        setCardChoices(actionCards);
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        List<Card> kept = new ArrayList<>();
        for(int i = 0; i < arguments.length; i++) {
            Card card = actionCards.get(arguments[i]);
            player.getHand().add(card);
            kept.add(card);
        }
        actionCards.removeAll(kept);
        player.getDiscard().addAll(actionCards);

        //keep calling the effect until the hand has enough cards
        if(player.getHandSize() < howMany) {
            gameState.getPendingEffects(player).push(new LibraryEffect(howMany));
        }
    }

}

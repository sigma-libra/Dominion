package shared.domain.effect.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.domain.cards.Card;
import shared.domain.effect.ChooseSomeEffect;
import shared.domain.effect.cardaction.CardAction;
import shared.domain.effect.cardsource.CardSource;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for interactive effects that require the Player to choose some cards.
 *
 * For example, a Chapel's "discard up to 4 cards from your hand" effect could be implemented with this.
 *
 * For your convenience, this class can be instantiated with predefined CardSources and CardActions.
 * A CardSource is responsible for generating a list of cards that the user can choose from. In the case of a Chapel, this would be the list of cards in the player's hand.
 * A CardAction is responsible for doing something with the cards the user selected. In the case of a Chapel, this would trash the chosen cards.
 */
public class ChooseCardsEffect extends ChooseSomeEffect<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private CardSource source;
    private CardAction action;

    //empty constructor required for Jackson deserialization
    public ChooseCardsEffect(){
        LOG.info("ChooseCardsEffect");
    }

    /**
     * Constructor for subclasses that don't want to use a CardSource and CardAction, and instead
     * override the updateChoices and errorchecked_execute methods
     *
     * @param from
     * @param upTo
     */
    protected ChooseCardsEffect(int from, int upTo){
        this(null, null, from, upTo);
    }

    /**
     * Instantiates a ChooseCardsEffect with a predefined CardSource and CardAction
     *
     * @param source The CardSource used to generate a list of cards which the player can choose from
     * @param action The CardAction that will handle the cards the player selects
     * @param from How many cards the player must select at minimum
     * @param upTo How many cards the player must select at maximum
     */
    public ChooseCardsEffect(CardSource source, CardAction action, int from, int upTo){
        super(new ArrayList<>(), from, upTo);

        LOG.info("ChooseCardsEffect");

        this.source = source;
        this.action = action;
    }

    /**
     * Convenience function to set the valid choices as a List<Card> instead of a List<Integer>
     *
     * @param choices A list of cards that the player can choose from
     */
    public void setCardChoices(Collection<Card> choices) {
        LOG.info("setCardChoices");
        List<Integer> ids = choices.stream().map(c -> c.getID()).collect(Collectors.toList());
        setChoices(ids);
    }

    public CardAction getAction() {
        return action;
    }

    @Override
    public void updateChoices(GameState gameState, Player player){
        LOG.info("updateChoices");
        setChoices(source.getChoices(gameState, player));
    }

    @Override
    public void errorchecked_execute(GameState gameState, Player player, int[] arguments) throws GameException {
        LOG.info("errorchecked_execute");
        List<Card> selectedCards = source.getAndRemoveChosenCards(gameState, player, getChoices(), arguments);
        action.execute(gameState, player, selectedCards);
    }

}

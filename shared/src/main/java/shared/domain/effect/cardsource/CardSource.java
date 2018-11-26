package shared.domain.effect.cardsource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import shared.domain.cards.Card;
import shared.domain.effect.cardsource.impl.DiscardPileSource;
import shared.domain.effect.cardsource.impl.HandCardsSource;
import shared.domain.effect.cardsource.impl.SupplyPileSource;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;

import java.util.List;

/**
 * Interface for use with ChooseCardsEffect
 *
 * See the ChooseCardsEffect documentation for details
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DiscardPileSource.class, name = "DiscardPileSource"),
    @JsonSubTypes.Type(value = HandCardsSource.class, name = "HandCardsSource"),
    @JsonSubTypes.Type(value = SupplyPileSource.class, name = "SupplyPileSource") })
public interface CardSource {
    /**
     * Generate a list of valid choices for the player to choose from
     *
     * @param gameState
     * @param player
     * @return list of card ids
     */
    List<Integer> getChoices(GameState gameState, Player player);

    /**
     * Remove the selected cards from the source and return a list of the removed Card instances
     *
     * @param gameState
     * @param player
     * @param possibleChoices The very same List<Integer> that you returned from getChoices
     * @param chosenIndices The indices of the cards the player selected
     * @return
     */
    List<Card> getAndRemoveChosenCards(GameState gameState, Player player, List<Integer> possibleChoices, int[] chosenIndices);
}

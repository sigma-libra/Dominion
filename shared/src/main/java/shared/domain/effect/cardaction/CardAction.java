package shared.domain.effect.cardaction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import shared.domain.cards.Card;
import shared.domain.effect.cardaction.impl.PutCardsInDiscardPileAction;
import shared.domain.effect.cardaction.impl.PutCardsOnDeckAction;
import shared.domain.effect.cardaction.impl.TrashCardsAction;
import shared.domain.engine.CardPile;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

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
    @JsonSubTypes.Type(value = PutCardsInDiscardPileAction.class, name = "PutCardsInDiscardPileAction"),
    @JsonSubTypes.Type(value = PutCardsOnDeckAction.class, name = "PutCardsOnDeckAction"),
    @JsonSubTypes.Type(value = TrashCardsAction.class, name = "TrashCardsAction") })
public interface CardAction {

    void execute(GameState gameState, Player player, List<Card> chosenCards) throws GameException;

}

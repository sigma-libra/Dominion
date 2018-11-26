package shared.domain.effect;

/**
 * @author Alex
 */
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import shared.domain.cards.kingdoms.Vassal;
import shared.domain.effect.impl.*;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

//instantiated classes need to be shared with client
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ChooseCardsEffect.class, name = "ChooseCardsEffect"),
    @JsonSubTypes.Type(value = TrashTargetCardsEffect.class, name = "TrashTargetCardsEffect"),
    @JsonSubTypes.Type(value = TrashTargetCardsOfGivenTypeEffect.class, name = "TrashTargetCardsOfGivenTypeEffect"),
    @JsonSubTypes.Type(value = TrashTargetCardAndFetchToHandGivenValueEffect.class, name = "TrashTargetCardAndFetchToHandGivenValueEffect"),
    @JsonSubTypes.Type(value = DiscardThenDrawEffect.class, name = "DiscardThenDrawEffect"),
    @JsonSubTypes.Type(value = PutTargetCardFromHandOnTopOfDeckEffect.class, name = "PutTargetCardFromHandOnTopOfDeckEffect"),
    @JsonSubTypes.Type(value = GainTargetCardToHandEffect.class, name = "GainTargetCardToHandEffect"),
    @JsonSubTypes.Type(value = GainTargetCardOfGivenTypeToHandEffect.class, name = "GainTargetCardOfGivenTypeToHandEffect"),
    @JsonSubTypes.Type(value = LibraryEffect.class, name = "LibraryEffect"),
    @JsonSubTypes.Type(value = VassalEffect.class, name = "VassalEffect"),
    @JsonSubTypes.Type(value = SentryTrashEffect.class, name = "SentryTrashEffect"),
    @JsonSubTypes.Type(value = SentryDiscardEffect.class, name = "SentryDiscardEffect"),
    @JsonSubTypes.Type(value = SentryReturnToDeckEffect.class, name = "SentryReturnToDeckEffect"),
    @JsonSubTypes.Type(value = TrashOrDiscardEffect.class, name = "TrashOrDiscardEffect"),
    @JsonSubTypes.Type(value = ThroneRoomEffect.class, name = "ThroneRoomEffect"),
    @JsonSubTypes.Type(value = DiscardCardsByNbEmptyPileEffect.class, name = "DiscardCardsByNbEmptyPileEffect"),
    @JsonSubTypes.Type(value = RevealCardsEffect.class, name = "RevealCardsEffect"),
    @JsonSubTypes.Type(value = PutTypeCardfromHandToDeckEffect.class, name = "PutTypeCardfromHandToDeckEffect"),
    @JsonSubTypes.Type(value = ThiefGainCardEffect.class, name = "ThiefGainCardEffect"),
    @JsonSubTypes.Type(value = ThiefTrashEffect.class, name = "ThiefTrashEffect")
})
public abstract class PlayerChoosesEffect extends CardEffect {

    /**
     * Called right before the user is asked to make a choice.
     * Can be used to create a list of currently valid choices. For example, an Effect that allows the player to buy a card would create a list of cards that can be bought.
     * @param gameState
     * @param player
     */
    public void updateChoices(GameState gameState, Player player){}

    /**
     * Executes the Effect with the choices the Player has made
     * @param gameState
     * @param player
     * @param arguments
     * @throws GameException
     */
    public abstract void execute(GameState gameState, Player player, int[] arguments) throws GameException;
}

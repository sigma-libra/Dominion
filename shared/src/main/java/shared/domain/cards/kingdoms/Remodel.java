package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.cards.TreasureCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.TrashTargetCardAndFetchToHandGivenValueEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a remodel card
 * Trash a card from hand and gain a new card costing up to 2 more than it
 */
public class Remodel extends KingdomCard {

    public Remodel() {
        super.price = 4;
        CardEffect[] effects = new CardEffect[1];
        effects[0] = new TrashTargetCardAndFetchToHandGivenValueEffect(0, 1, Card.class, 2);
        setEffects(effects);
    }
}

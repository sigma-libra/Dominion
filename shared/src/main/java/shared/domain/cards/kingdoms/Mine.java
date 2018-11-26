package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.cards.TreasureCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.GainTargetCardToHandEffect;
import shared.domain.effect.impl.TrashTargetCardAndFetchToHandGivenValueEffect;
import shared.domain.effect.impl.TrashTargetCardsOfGivenTypeEffect;

/**
 * class modeling a mine card
 *
 * Allows trashing a treasure card from hand and gaining a card of value up to 3 more than
 * trashed card
 *
 */
public class Mine extends KingdomCard {

    public Mine() {
        super.price = 5;

        CardEffect[] effects = new CardEffect[1];
        effects[0] = new TrashTargetCardAndFetchToHandGivenValueEffect(0, 1, TreasureCard.class, 3);
        setEffects(effects);
    }
}

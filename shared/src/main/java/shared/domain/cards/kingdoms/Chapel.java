package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.TrashTargetCardsEffect;

/**
 * class modeling a chapel card: trash up to 4 cards from hand
 */
public class Chapel extends KingdomCard {
    public Chapel() {
        price = 2;

        CardEffect[] effects = new CardEffect[1];
        effects[0] = new TrashTargetCardsEffect(0, 4);
        setEffects(effects);
    }
}

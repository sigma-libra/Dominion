package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a laboratory card:
 * + 2 cards
 * + 1 Action
 */
public class Laboratory extends KingdomCard {
    public Laboratory() {
        price = 5;

        CardEffect[] cardEffects = new CardEffect[2];
        cardEffects[0] = new DrawCardsEffect(2);
        cardEffects[1] = new AddActionsEffect(1);
        setEffects(cardEffects);
    }
}

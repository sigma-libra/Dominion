package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.DrawCardsEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a smithy card:
 * + 3 cards
 */
public class Smithy extends KingdomCard {

    public Smithy() {
        CardEffect[] cardEffects = new CardEffect[1];
        cardEffects[0] = new DrawCardsEffect(3);
        setEffects(cardEffects);
        super.price = 4;
    }
}

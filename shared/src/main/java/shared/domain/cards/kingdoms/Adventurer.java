package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.DiscardUntilTwoTreasureCards;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * Adventurer card: reveal cards from deck until two treasure cards are revealed.
 * These go in hand, the rest go on to discard
 */
public class Adventurer extends KingdomCard {

    public Adventurer() {
        super.price = 6;

        CardEffect[] effects = new CardEffect[1];
        effects[0] = new DiscardUntilTwoTreasureCards();
        this.setEffects(effects);
    }
}

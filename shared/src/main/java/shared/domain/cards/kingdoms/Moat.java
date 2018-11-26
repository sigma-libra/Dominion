package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a moat card
 * + 2 cards
 * Note: Moat also has a protective effect when in hand: attacks have no effect
 * But this is implemented in the attack effects instead
 */
public class Moat extends KingdomCard {

    public Moat() {
        super.price = 2;
        CardEffect[] effects = new CardEffect[1];
        effects[0] = new DrawCardsEffect(2);
        setEffects(effects);
    }
}

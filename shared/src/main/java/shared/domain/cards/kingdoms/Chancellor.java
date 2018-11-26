package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.PutDeckIntoDiscardPile;

/**
 * Chancellor card: immediately put deck into discard
 * +2 credit
 */
public class Chancellor extends KingdomCard{

    public Chancellor() {
        super.price = 3;

        CardEffect[] effects = new CardEffect[2];
        effects[0] = new AddCreditEffect(2);
        effects[1] = new PutDeckIntoDiscardPile();
        setEffects(effects);
    }
}

package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddBuysEffect;
import shared.domain.effect.impl.AddCreditEffect;

/**
 * Woodcutter card:
 * +1 buy
 * +2 credit
 */
public class Woodcutter extends KingdomCard{

    public Woodcutter() {
        super.price = 3;
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new AddBuysEffect(1);
        effects[1] = new AddCreditEffect(2);
        setEffects(effects);
    }
}

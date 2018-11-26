package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.MilitiaEffect;
import shared.domain.engine.GameState;

/**
 * class moideling a milita card:
 * + 2 coin
 * each other player discards down to 3 cards in hand
 */
public class Militia extends KingdomCard {

    public Militia() {
        super.price = 4;
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new AddCreditEffect(2);
        effects[1] = new MilitiaEffect();
        setEffects(effects);
    }
}

package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.VassalEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a vassal card:
 * + 2 credit
 * discard the top of the deck. If it's an action card, choose whether to play it
 */
public class Vassal extends KingdomCard {

    public Vassal() {
        super.price = 3;
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new AddCreditEffect(2);
        effects[1] = new VassalEffect();
        setEffects(effects);
    }
}

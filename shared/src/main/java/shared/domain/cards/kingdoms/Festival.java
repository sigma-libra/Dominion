package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.AddBuysEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.DrawCardsEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a festival card:
 * +2 actions
 * +1 buy
 * +2 credit
 */
public class Festival extends KingdomCard {
    public Festival() {
        CardEffect[] cardEffects = new CardEffect[3];
        cardEffects[0] = new AddActionsEffect(2);
        cardEffects[1] = new AddBuysEffect(1);
        cardEffects[2] = new AddCreditEffect(2);
        setEffects(cardEffects);
        super.price = 5;
    }
}

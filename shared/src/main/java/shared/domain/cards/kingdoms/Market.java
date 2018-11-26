package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.AddBuysEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a market card:
 * + 1 card
 * + 1 Action
 * + 1 Buy
 * + 1 credit
 */
public class Market extends KingdomCard {
    public Market() {
        CardEffect[] cardEffects = new CardEffect[4];
        cardEffects[0] = new DrawCardsEffect(1);
        cardEffects[1] = new AddActionsEffect(1);
        cardEffects[2] = new AddBuysEffect(1);
        cardEffects[3] = new AddCreditEffect(1);
        setEffects(cardEffects);
        super.price = 5;
    }
}

package shared.domain.cards.kingdoms;


import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a village card:
 * + 1 card
 * + 2 actions
 */
public class Village extends KingdomCard {

    public Village() {
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new DrawCardsEffect(1);
        effects[1] = new AddActionsEffect(2);
        setEffects(effects);
        super.price = 3;
    }
}

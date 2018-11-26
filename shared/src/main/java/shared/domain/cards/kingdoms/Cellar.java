package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DiscardThenDrawEffect;

/**
 * Class modelling a Cellar Card: discard any number of cards, then draw that many
 */
public class Cellar extends KingdomCard {

    public Cellar() {
        price = 2;

        CardEffect[] effects = new CardEffect[2];
        effects[0] = new AddActionsEffect(1);
        effects[1] = new DiscardThenDrawEffect();
        setEffects(effects);
    }
}

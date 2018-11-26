package shared.domain.cards.kingdoms;


import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.GainTargetCardToHandEffect;
import shared.domain.effect.impl.PutTargetCardFromHandOnTopOfDeckEffect;

/**
 * class modeling an artisan card: gain a card costing up to 5 and put a card from hand onto deck
 */
public class Artisan extends KingdomCard {

    public Artisan() {
        super.price = 6;

        CardEffect[] effects = new CardEffect[2];
        effects[0] = new GainTargetCardToHandEffect(5);
        effects[1] = new PutTargetCardFromHandOnTopOfDeckEffect(1);
        setEffects(effects);
    }
}

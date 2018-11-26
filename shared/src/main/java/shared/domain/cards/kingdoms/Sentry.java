package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DrawCardsEffect;
import shared.domain.effect.impl.SentryTrashEffect;

/**
 * class modeling a sentry card:
 * + 1 card
 * + 1 action
 * Check top two cards on deck to trash/discard or put back in any order
 */
public class Sentry extends KingdomCard {

    public Sentry() {
        super.price = 5;
        CardEffect[] effects = new CardEffect[3];
        effects[0] = new DrawCardsEffect(1);
        effects[1] = new AddActionsEffect(1);
        effects[2] = new SentryTrashEffect(2);
        setEffects(effects);
    }
}

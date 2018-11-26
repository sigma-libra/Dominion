package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.ThiefEffect;

/**
 * Thief card:
 * Each other player reveals two treasure cards from hand
 * For each player, choose one to either trash or keep
 */
public class Thief extends KingdomCard{

    public Thief() {
        super.price = 4;

        CardEffect[] effects = new CardEffect[1];
        effects[0] = new ThiefEffect();
        setEffects(effects);
    }
}

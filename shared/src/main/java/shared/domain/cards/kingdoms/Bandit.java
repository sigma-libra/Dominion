package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.cards.treasures.GoldCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.BanditEffect;
import shared.domain.effect.impl.GainCardOfTypeToDiscard;


/**
 * class modeling a bandit card:
 * Player gains a gold; All the other players
 * 1) Show the top two cards of their deck
 * 2) If one of the revealed cards is a silver of gold, trash it
 * 3) Discard the rest
 *
 */
public class Bandit extends KingdomCard {

    public Bandit() {
        super.price = 5;
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new GainCardOfTypeToDiscard<>(GoldCard.class);
        effects[1] = new BanditEffect();
        setEffects(effects);
    }
}

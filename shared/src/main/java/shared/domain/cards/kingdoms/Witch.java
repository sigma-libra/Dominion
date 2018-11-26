package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCursesToEveryoneExceptCurrentPlayerEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a witch card:
 * + 2 cards
 * Each other player gets a curse
 */
public class Witch extends KingdomCard {

    public Witch() {
        super.price = 5;

        CardEffect[] effects = new CardEffect[2];
        effects[0] = new DrawCardsEffect(2);
        effects[1] = new AddCursesToEveryoneExceptCurrentPlayerEffect();
        setEffects(effects);
    }
}

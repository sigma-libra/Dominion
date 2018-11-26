package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.BureaucratEffect;
import shared.domain.effect.impl.GainOfTypeOnPlayerDeck;
import shared.domain.effect.impl.RevealCardsEffect;

/**
 * class modeling a bureaucrat card: gain a silver
 * Each other player reveals a Victory card and puts it on deck
 */
public class Bureaucrat extends KingdomCard {
    public Bureaucrat() {
        super.price = 4;

        CardEffect[] effects = new CardEffect[2];
        effects[0] = new GainOfTypeOnPlayerDeck(12);
        effects[1] = new BureaucratEffect();
        this.setEffects(effects);
    }
}

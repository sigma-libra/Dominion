package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.ChooseCardsEffect;
import shared.domain.effect.cardaction.impl.PutCardsInDiscardPileAction;
import shared.domain.effect.cardsource.impl.SupplyPileSource;

/**
 * class modeling a workshop card
 * Gain new card of price up to 4
 */
public class Workshop extends KingdomCard {

    public Workshop() {
        price = 3;

        CardEffect[] effects = new CardEffect[1];
        effects[0] = new ChooseCardsEffect(new SupplyPileSource(4), new PutCardsInDiscardPileAction(), 1, 1);
        setEffects(effects);
    }
}

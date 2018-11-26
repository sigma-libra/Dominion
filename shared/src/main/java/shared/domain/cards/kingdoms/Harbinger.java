package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.ChooseCardsEffect;
import shared.domain.effect.cardaction.impl.PutCardsOnDeckAction;
import shared.domain.effect.cardsource.impl.DiscardPileSource;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DrawCardsEffect;

/**
 * class modeling a harbinger card:
 * +1 Action
 * Move up to 1 card from discard to deck
 *
 */
public class Harbinger extends KingdomCard {

    public Harbinger() {
        price = 3;

        CardEffect[] effects = new CardEffect[3];
        effects[0] = new DrawCardsEffect(1);
        effects[1] = new AddActionsEffect(1);
        effects[2] = new ChooseCardsEffect(new DiscardPileSource(), new PutCardsOnDeckAction(), 0, 1);
        setEffects(effects);
    }

}

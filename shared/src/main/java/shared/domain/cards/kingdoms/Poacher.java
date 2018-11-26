package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.cards.TreasureCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.*;
import shared.domain.engine.GameState;

/**
 * class modeling a poacher card:
 * + 1 card
 * + 1 action
 * + 1 credit
 * Discard one card per empty supply pile
 *
 */
public class Poacher extends KingdomCard {

    public Poacher() {
        super.price = 4;
        CardEffect[] effects = new CardEffect[4];
        effects[0] = new DrawCardsEffect(1);
        effects[1] = new AddActionsEffect(1);
        effects[2] = new AddCreditEffect(1);
        effects[3] = new DiscardCardsByNbEmptyPileEffect();
        setEffects(effects);
    }
}

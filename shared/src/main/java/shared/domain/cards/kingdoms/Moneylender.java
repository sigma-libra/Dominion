package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.cards.TreasureCard;
import shared.domain.cards.treasures.CopperCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddCreditEffect;
import shared.domain.effect.impl.TrashCopperCardForCoinEffect;
import shared.domain.effect.impl.TrashTargetCardAndFetchToHandGivenValueEffect;
import shared.domain.effect.impl.TrashTargetCardsOfGivenTypeEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a moneylender card (trash a copper card from hand for 3 coins)
 */
public class Moneylender extends KingdomCard {

    public Moneylender() {
        super.price = 4;
        CardEffect[] effects = new CardEffect[1];
        effects[0] = new TrashCopperCardForCoinEffect(3);
        setEffects(effects);
    }
}

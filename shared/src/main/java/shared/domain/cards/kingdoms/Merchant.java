package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddActionsEffect;
import shared.domain.effect.impl.DiscardThenDrawEffect;
import shared.domain.effect.impl.DrawCardsEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a merchant card
 * actions: +1 Card, +1 Action and the first time a silver card is played this turn, player gains
 * +1 credit
 */
public class Merchant extends KingdomCard {

    boolean alreadyAddedCredit;

    public Merchant() {
        super.price = 3;
        alreadyAddedCredit = false;
        CardEffect[] effects = new CardEffect[2];
        effects[0] = new DrawCardsEffect(1);
        effects[1] = new AddActionsEffect(1);
        setEffects(effects);
    }


    public boolean isAlreadyAddedCredit() {
        return alreadyAddedCredit;
    }

    public void setAlreadyAddedCredit(boolean alreadyAddedCredit) {
        this.alreadyAddedCredit = alreadyAddedCredit;
    }

}

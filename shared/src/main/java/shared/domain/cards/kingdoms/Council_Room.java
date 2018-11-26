package shared.domain.cards.kingdoms;

import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.AddBuysEffect;
import shared.domain.effect.impl.DrawCardsEffect;
import shared.domain.effect.impl.EachOtherPlayerEffect;

/**
 * class modeling a councilRoom Card:
 * +4 cards
 * + 1 buy
 * Each other player draws a card
 */
public class Council_Room extends KingdomCard {
    public Council_Room() {
        super.price = 5;

        CardEffect[] effects = new CardEffect[3];
        effects[0] = new DrawCardsEffect(4);
        effects[1] = new AddBuysEffect(1);
        effects[2] = new EachOtherPlayerEffect(new DrawCardsEffect(1));
        setEffects(effects);
    }
}

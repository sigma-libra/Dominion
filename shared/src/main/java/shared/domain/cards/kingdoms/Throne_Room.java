package shared.domain.cards.kingdoms;

import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.ThroneRoomEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a throne room card:
 * play an action card from hand twice
 */
public class Throne_Room extends KingdomCard {

    public Throne_Room() {
        super.price = 4;
        CardEffect[] effects = new CardEffect[1];
        effects[0] = new ThroneRoomEffect(0,1);
        setEffects(effects);
    }
}

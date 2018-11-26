package shared.domain.cards.kingdoms;

import javafx.scene.effect.Effect;
import shared.domain.cards.Card;
import shared.domain.cards.KingdomCard;
import shared.domain.effect.CardEffect;
import shared.domain.effect.impl.LibraryEffect;
import shared.domain.engine.GameState;

/**
 * class modeling a library card:
 * draw until 7 cards in hand, skipping any action cards if wanted
 */
public class Library extends KingdomCard {

    public Library() {
        super.price = 5;
        CardEffect[] effects = new CardEffect[1];
        effects[0] = new LibraryEffect(7);
        setEffects(effects);

    }
}

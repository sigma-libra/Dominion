package shared.domain.effect.impl;

import shared.domain.cards.Card;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.EmptyCardDeckException;
import shared.domain.exceptions.GameException;


public class PutDeckIntoDiscardPile extends NoPlayerChoosesEffect {
    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        try {
            while (true) {
                player.discardCard(player.getDeck().drawCard());
            }
        } catch (EmptyCardDeckException e) {
            // No more cards
        }
    }
}

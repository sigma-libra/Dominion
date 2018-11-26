package shared.domain.effect.impl;

import shared.domain.cards.Card;
import shared.domain.effect.NoPlayerChoosesEffect;
import shared.domain.engine.GameState;
import shared.domain.engine.Player;
import shared.domain.exceptions.GameException;

import java.util.List;
import java.util.Stack;

public class GainCardOfTypeToDiscard <C extends Card> extends NoPlayerChoosesEffect{

    private Class<C> type;

    public GainCardOfTypeToDiscard(Class<C> type) {
        super();
        this.type = type;

    }

    @Override
    public void execute(GameState gameState, Player player) throws GameException {
        for (Stack<Card> pile : gameState.getSupply().getPiles()){
            if (!pile.isEmpty()){
                Card card = pile.peek();
                if (type.isInstance(card)) {
                    player.getDiscard().add(gameState.getSupply().retrieveCardById(card.getID()));
                    String message = player.getUser().getUserName() + " gained another " + card.getName();
                    gameState.getPlayLog().add(message);
                }
            }
        }
    }
}
